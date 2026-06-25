const API = "http://localhost:8080/api";

// ── Состояние ──────────────────────────────────────────────────────────────
// Храним в памяти — никакого localStorage
let authToken = null;
let currentRole = null;
let currentUsername = null;
let allUsers = [];

// ── Утилиты ────────────────────────────────────────────────────────────────

function flash(msg, type = "success") {
  const el = document.getElementById("flash");
  el.textContent = msg;
  el.className = "flash show " + type;
  setTimeout(() => el.classList.remove("show"), 2800);
}

function clearErrors() {
  document.querySelectorAll(".err-msg").forEach((e) => {
    e.textContent = "";
    e.classList.remove("show");
  });
  document.querySelectorAll("input.error").forEach((e) => e.classList.remove("error"));
}

function showFieldErrors(data) {
  const map = {
    fullName: ["u-name", "err-u-name"],
    email: ["u-email", "err-u-email"],
    documentType: ["d-type", "err-d-type"],
    documentNumber: ["d-number", "err-d-number"],
    userId: ["d-user", "err-d-user"],
  };
  Object.entries(data).forEach(([field, msg]) => {
    if (map[field]) {
      const [inputId, errId] = map[field];
      document.getElementById(inputId)?.classList.add("error");
      const err = document.getElementById(errId);
      if (err) {
        err.textContent = msg;
        err.classList.add("show");
      }
    }
  });
}

// ── API-запросы с токеном ──────────────────────────────────────────────────

async function api(method, path, body) {
  const headers = { "Content-Type": "application/json" };

  // Добавляем JWT в каждый запрос
  if (authToken) headers["Authorization"] = "Bearer " + authToken;

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(API + path, opts);

  // Токен протух или невалиден — разлогиниваем
  if (res.status === 401) {
    logout();
    return { ok: false, status: 401, data: null };
  }

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  return { ok: res.ok, status: res.status, data };
}

// ── AUTH ───────────────────────────────────────────────────────────────────

function switchAuthTab(tab) {
  document.getElementById("tab-login").style.display = tab === "login" ? "" : "none";
  document.getElementById("tab-register").style.display = tab === "register" ? "" : "none";
  document.querySelectorAll(".auth-tab").forEach((el, i) =>
      el.classList.toggle("active", (i === 0) === (tab === "login")));
  document.getElementById("auth-error").classList.remove("show");
}

async function doLogin() {
  const username = document.getElementById("login-username").value.trim();
  const password = document.getElementById("login-password").value;

  const res = await fetch(API + "/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (res.ok) {
    const data = await res.json();
    enterApp(data);
  } else {
    showAuthError("Invalid username or password");
  }
}

async function doRegister() {
  const username = document.getElementById("reg-username").value.trim();
  const password = document.getElementById("reg-password").value;

  const res = await fetch(API + "/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (res.ok) {
    const data = await res.json();
    enterApp(data);
  } else {
    const err = await res.json();
    showAuthError(err.message || "Registration failed");
  }
}

function enterApp(data) {
  // Сохраняем токен и данные пользователя в памяти
  authToken = data.token;
  currentRole = data.role;
  currentUsername = data.username;

  // Показываем имя и роль в шапке
  document.getElementById("header-username").textContent = currentUsername;
  const roleEl = document.getElementById("header-role");
  roleEl.textContent = currentRole;
  roleEl.className = "role-tag " + (currentRole === "ADMIN" ? "role-admin" : "role-user");

  // Показываем/скрываем элементы по роли
  const isAdmin = currentRole === "ADMIN";
  document.querySelectorAll(".admin-only").forEach((el) => (el.style.display = isAdmin ? "" : "none"));

  // Если не ADMIN — сетка без левой панели
  if (!isAdmin) {
    document.getElementById("users-list-panel").style.gridColumn = "1 / -1";
  }

  document.getElementById("auth-screen").style.display = "none";
  document.getElementById("app").style.display = "block";

  loadUsers();
}

function logout() {
  // Очищаем токен из памяти
  authToken = currentRole = currentUsername = null;
  allUsers = [];

  document.getElementById("app").style.display = "none";
  document.getElementById("auth-screen").style.display = "flex";
  document.getElementById("login-username").value = "";
  document.getElementById("login-password").value = "";
}

function showAuthError(msg) {
  const el = document.getElementById("auth-error");
  el.textContent = msg;
  el.classList.add("show");
}

// ── USERS ──────────────────────────────────────────────────────────────────

async function loadUsers() {
  const { data } = await api("GET", "/users");
  allUsers = data || [];
  document.getElementById("users-count").textContent = allUsers.length;

  const wrap = document.getElementById("users-table-wrap");
  if (!allUsers.length) {
    wrap.innerHTML = '<div class="empty">No users found</div>';
    return;
  }

  const isAdmin = currentRole === "ADMIN";

  wrap.innerHTML = `<table>
    <thead><tr>
      <th>ID</th><th>Full name</th><th>Email</th>
      ${isAdmin ? "<th>Actions</th>" : ""}
    </tr></thead>
    <tbody>${allUsers
      .map(
          (u) => `<tr>
      <td class="id-cell">#${u.id}</td>
      <td>${u.fullName}</td>
      <td style="color:var(--muted)">${u.email}</td>
      ${
              isAdmin
                  ? `<td><div class="actions">
        <button class="btn btn-ghost" onclick="openEditUser(${u.id},'${esc(u.fullName)}','${esc(u.email)}')">Edit</button>
        <button class="btn btn-danger" onclick="deleteUser(${u.id})">Del</button>
      </div></td>`
                  : ""
          }
    </tr>`,
      )
      .join("")}</tbody>
  </table>`;
}

async function createUser() {
  clearErrors();
  const fullName = document.getElementById("u-name").value.trim();
  const email = document.getElementById("u-email").value.trim();

  const { ok, status, data } = await api("POST", "/users", { fullName, email });

  if (ok) {
    document.getElementById("u-name").value = "";
    document.getElementById("u-email").value = "";
    flash("User created → #" + data.id);
    loadUsers();
  } else if (status === 400 && data?.fieldErrors) {
    showFieldErrors(data.fieldErrors);
    flash("Validation error", "error");
  } else if (status === 403) {
    flash("Access denied — admin only", "error");
  }
}

function openEditUser(id, name, email) {
  document.getElementById("edit-u-id").value = id;
  document.getElementById("edit-u-name").value = name;
  document.getElementById("edit-u-email").value = email;
  document.getElementById("modal-user").classList.add("open");
}

async function updateUser() {
  const id = document.getElementById("edit-u-id").value;
  const fullName = document.getElementById("edit-u-name").value.trim();
  const email = document.getElementById("edit-u-email").value.trim();

  const { ok, status } = await api("PUT", "/users/" + id, { fullName, email });

  if (ok) {
    closeModal("modal-user");
    flash("User updated");
    loadUsers();
  } else if (status === 403) flash("Access denied — admin only", "error");
  else flash("Validation error", "error");
}

async function deleteUser(id) {
  const { ok, status } = await api("DELETE", "/users/" + id);
  if (ok) {
    flash("User deleted");
    loadUsers();
  } else if (status === 403) flash("Access denied — admin only", "error");
  else flash("User not found", "error");
}

// ── DOCUMENTS ──────────────────────────────────────────────────────────────

async function loadDocuments() {
  const { data } = await api("GET", "/documents");
  renderDocs(data || []);
}

async function searchDocuments() {
  const type = document.getElementById("search-type").value.trim();
  if (!type) {
    loadDocuments();
    return;
  }
  const { data } = await api("GET", "/documents/search?type=" + encodeURIComponent(type));
  renderDocs(data || []);
}

function renderDocs(docs) {
  document.getElementById("docs-count").textContent = docs.length;
  const wrap = document.getElementById("docs-table-wrap");
  const isAdmin = currentRole === "ADMIN";

  if (!docs.length) {
    wrap.innerHTML = '<div class="empty">No documents found</div>';
    return;
  }

  wrap.innerHTML = `<table>
    <thead><tr>
      <th>ID</th><th>Type</th><th>Number</th><th>Owner</th>
      ${isAdmin ? "<th>Actions</th>" : ""}
    </tr></thead>
    <tbody>${docs
      .map(
          (d) => `<tr>
      <td class="id-cell">#${d.id}</td>
      <td><span class="type-tag">${d.documentType}</span></td>
      <td>${d.documentNumber}</td>
      <td style="color:var(--muted)">${d.ownerName}</td>
      ${
              isAdmin
                  ? `<td><div class="actions">
        <button class="btn btn-ghost" onclick="openEditDoc(${d.id},'${esc(d.documentType)}','${esc(d.documentNumber)}')">Edit</button>
        <button class="btn btn-danger" onclick="deleteDocument(${d.id})">Del</button>
      </div></td>`
                  : ""
          }
    </tr>`,
      )
      .join("")}</tbody>
  </table>`;
}

async function loadUsersForSelect() {
  if (!allUsers.length) {
    const { data } = await api("GET", "/users");
    allUsers = data || [];
  }
  ["d-user", "edit-d-user"].forEach((id) => {
    const sel = document.getElementById(id);
    if (!sel) return;
    sel.innerHTML =
        '<option value="">— select user —</option>' +
        allUsers.map((u) => `<option value="${u.id}">${u.fullName}</option>`).join("");
  });
}

async function createDocument() {
  clearErrors();
  const documentType = document.getElementById("d-type").value.trim();
  const documentNumber = document.getElementById("d-number").value.trim();
  const userId = document.getElementById("d-user").value;

  const { ok, status, data } = await api("POST", "/documents", {
    documentType,
    documentNumber,
    userId: userId ? Number(userId) : null,
  });

  if (ok) {
    document.getElementById("d-type").value = "";
    document.getElementById("d-number").value = "";
    document.getElementById("d-user").value = "";
    flash("Document created → #" + data.id);
    loadDocuments();
  } else if (status === 400 && data?.fieldErrors) {
    showFieldErrors(data.fieldErrors);
    flash("Validation error", "error");
  } else if (status === 403) {
    flash("Access denied", "error");
  }
}

function openEditDoc(id, type, number) {
  document.getElementById("edit-d-id").value = id;
  document.getElementById("edit-d-type").value = type;
  document.getElementById("edit-d-number").value = number;
  loadUsersForSelect();
  document.getElementById("modal-doc").classList.add("open");
}

async function updateDocument() {
  const id = document.getElementById("edit-d-id").value;
  const documentType = document.getElementById("edit-d-type").value.trim();
  const documentNumber = document.getElementById("edit-d-number").value.trim();
  const userId = document.getElementById("edit-d-user").value;

  const { ok, status } = await api("PUT", "/documents/" + id, {
    documentType,
    documentNumber,
    userId: userId ? Number(userId) : null,
  });

  if (ok) {
    closeModal("modal-doc");
    flash("Document updated");
    loadDocuments();
  } else if (status === 403) flash("Access denied — admin only", "error");
  else flash("Validation error", "error");
}

async function deleteDocument(id) {
  const { ok, status } = await api("DELETE", "/documents/" + id);
  if (ok) {
    flash("Document deleted");
    loadDocuments();
  } else if (status === 403) flash("Access denied — admin only", "error");
  else flash("Document not found", "error");
}

// ── Утилиты ────────────────────────────────────────────────────────────────

function showPage(name, event) {
  document.querySelectorAll(".page").forEach((p) => p.classList.remove("active"));
  document.querySelectorAll(".tab").forEach((t) => t.classList.remove("active"));
  document.getElementById("page-" + name).classList.add("active");
  event.target.classList.add("active");
  if (name === "documents") {
    loadDocuments();
    loadUsersForSelect();
  }
}

function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}

function esc(s) {
  return String(s).replace(/'/g, "\\'");
}
