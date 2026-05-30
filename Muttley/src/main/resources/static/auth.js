const BASE_URL = 'http://localhost:8080';

function getToken() {
  return sessionStorage.getItem('token');
}

function authFetch(url, options = {}) {
  const token = getToken();
  if (!token) {
    window.location.href = 'login.html';
    return Promise.reject('Não autenticado');
  }
  return fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...(options.headers || {})
    }
  }).then(res => {
    if (res.status === 401 || res.status === 403) {
      sessionStorage.removeItem('token');
      window.location.href = 'login.html';
      return Promise.reject('Sessão expirada');
    }
    return res;
  });
}

function checkAuth() {
  if (!getToken()) {
    window.location.href = 'login.html';
  }
}

function logout() {
  sessionStorage.removeItem('token');
  window.location.href = 'login.html';
}