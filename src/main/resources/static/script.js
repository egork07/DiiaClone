const API = "http://localhost:8080/api";
let allUsers = [];

function showPage(name) {
  document
    .querySelectorAll(".page")
    .forEach((p) => p.classList.remove("active"));
  document
    .querySelectorAll(".tab")
    .forEach((t) => t.classList.remove("active"));
  document.getElementById("page-" + name).classList.add("active");
  event.target.classList.add("active");
  if (name === "documents") {
    loadDocuments();
    loadUsersForSelect();
  }
}

function flash(msg, type = "success") {
  const el = document.getElementById("flash");
  el.textContent = msg;
  el.className = "flash show " + type;
  setTimeout(() => el.classList.remove("show"), 2800);
}

function clearErrors(fields) {
  fields.forEach((id) => {
    const inp = document.getElementById(id);
    if (inp) inp.classList.remove("error");
  });
  document.querySelectorAll(".err-msg").forEach((e) => {
    e.textContent = "";
    e.classList.remove("show");
  });
}

function showError(field, errId, msg) {
  const inp = document.getElementById(field);
  const err = document.getElementById(errId);
  if (inp) inp.classList.add("error");
  if (err) {
    err.textContent = msg;
    err.classList.add("show");
  }
}

async function api(method, path, body) {
  const opts = {
    method,
    headers: { "Content-Type": "application/json" },
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API + path, opts);
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  return { ok: res.ok, status: res.status, data };
}


async function loadUsers() {
  const { data } = await api("GET", "/users");
  allUsers = data || [];
  document.getElementById("users-count").textContent = allUsers.length;

  const wrap = document.getElementById("users-table-wrap");
  if (!allUsers.length) {
    wrap.innerHTML = '<div class="empty">No users found</div>';
    return;
  }

  wrap.innerHTML = `<table>
    <thead><tr>
      <th>ID</th><th>Full name</th><th>Email</th><th>Actions</th>
    </tr></thead>
    <tbody>${allUsers
      .map(
        (u) => `<tr>
      <td class="id-cell">#${u.id}</td>
      <td>${u.fullName}</td>
      <td style="color:var(--muted)">${u.email}</td>
      <td><div class="actions">
        <button class="btn btn-ghost" onclick="openEditUser(${u.id},'${esc(u.fullName)}','${esc(u.email)}')">Edit</button>
        <button class="btn btn-danger" onclick="deleteUser(${u.id})">Del</button>
      </div></td>
    </tr>`,
      )
      .join("")}</tbody>
  </table>`;
}

async function createUser() {
  clearErrors(["u-name", "u-email"]);
  const fullName = document.getElementById("u-name").value.trim();
  const email = document.getElementById("u-email").value.trim();

  const { ok, data } = await api("POST", "/users", { fullName, email });
  if (ok) {
    document.getElementById("u-name").value = "";
    document.getElementById("u-email").value = "";
    flash("User created → #" + data.id);
    loadUsers();
  } else {
    if (data.fullName) showError("u-name", "err-u-name", data.fullName);
    if (data.email) showError("u-email", "err-u-email", data.email);
    flash("Validation error", "error");
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
  const { ok } = await api("PUT", "/users/" + id, { fullName, email });
  if (ok) {
    closeModal("modal-user");
    flash("User updated");
    loadUsers();
  } else flash("Validation error", "error");
}

async function deleteUser(id) {
  const { ok } = await api("DELETE", "/users/" + id);
  if (ok) {
    flash("User deleted");
    loadUsers();
  } else flash("User not found", "error");
}

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
  const { data } = await api(
    "GET",
    "/documents/search?type=" + encodeURIComponent(type),
  );
  renderDocs(data || []);
}

function renderDocs(docs) {
  document.getElementById("docs-count").textContent = docs.length;
  const wrap = document.getElementById("docs-table-wrap");
  if (!docs.length) {
    wrap.innerHTML = '<div class="empty">No documents found</div>';
    return;
  }

  wrap.innerHTML = `<table>
    <thead><tr>
      <th>ID</th><th>Type</th><th>Number</th><th>Owner</th><th>Actions</th>
    </tr></thead>
    <tbody>${docs
      .map(
        (d) => `<tr>
      <td class="id-cell">#${d.id}</td>
      <td><span class="type-tag">${d.documentType}</span></td>
      <td>${d.documentNumber}</td>
      <td style="color:var(--muted)">${d.ownerName}</td>
      <td><div class="actions">
        <button class="btn btn-ghost" onclick="openEditDoc(${d.id},'${esc(d.documentType)}','${esc(d.documentNumber)}')">Edit</button>
        <button class="btn btn-danger" onclick="deleteDocument(${d.id})">Del</button>
      </div></td>
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
      allUsers
        .map((u) => `<option value="${u.id}">${u.fullName}</option>`)
        .join("");
  });
}

async function createDocument() {
  clearErrors(["d-type", "d-number", "d-user"]);
  const documentType = document.getElementById("d-type").value.trim();
  const documentNumber = document.getElementById("d-number").value.trim();
  const userId = document.getElementById("d-user").value;

  const { ok, data } = await api("POST", "/documents", {
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
  } else {
    if (data.documentType) showError("d-type", "err-d-type", data.documentType);
    if (data.documentNumber)
      showError("d-number", "err-d-number", data.documentNumber);
    if (data.userId) showError("d-user", "err-d-user", data.userId);
    flash("Validation error", "error");
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
  const { ok } = await api("PUT", "/documents/" + id, {
    documentType,
    documentNumber,
    userId: userId ? Number(userId) : null,
  });
  if (ok) {
    closeModal("modal-doc");
    flash("Document updated");
    loadDocuments();
  } else flash("Validation error", "error");
}

async function deleteDocument(id) {
  const { ok } = await api("DELETE", "/documents/" + id);
  if (ok) {
    flash("Document deleted");
    loadDocuments();
  } else flash("Document not found", "error");
}


function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}

function esc(s) {
  return String(s).replace(/'/g, "\\'");
}


loadUsers();
