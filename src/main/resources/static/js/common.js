/**
 * common.js — Creators Studio Shared Utilities
 * API helper, sessionStorage auth, toast notifications, formatters.
 */

const BASE_URL = '';   // Same origin — Spring Boot serves API and static files

// ─── Session / Auth ───────────────────────────────────────────────────────────

const SESSION_KEY = 'cs_user';

function saveSession(userResponse) {
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(userResponse));
}

function getSession() {
    const raw = sessionStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
}

function clearSession() {
    sessionStorage.removeItem(SESSION_KEY);
}

/**
 * Guards a page: if no session, redirects to login.
 * Call at the top of every protected page's JS.
 */
function requireAuth() {
    if (!getSession()) {
        window.location.href = '/pages/login.html';
    }
}

function logout() {
    clearSession();
    window.location.href = '/pages/login.html';
}

// ─── API Helper ───────────────────────────────────────────────────────────────

/**
 * Unified fetch wrapper.
 * @param {string} method  HTTP method (GET, POST, PUT, DELETE)
 * @param {string} url     API path e.g. '/api/purchases/fabrics'
 * @param {object} [body]  Request body (will be JSON-serialized)
 * @returns {Promise<object>} Parsed JSON (the full ApiResponse body)
 */
async function apiCall(method, url, body = null) {
    const opts = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body !== null) {
        opts.body = JSON.stringify(body);
    }
    const res = await fetch(BASE_URL + url, opts);
    const json = await res.json();
    if (!res.ok || !json.success) {
        throw new Error(json.message || `Request failed (${res.status})`);
    }
    return json;  // { success, message, data }
}

// ─── Toast Notifications ──────────────────────────────────────────────────────

function showToast(message, type = 'success', durationMs = 3500) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s';
        setTimeout(() => toast.remove(), 350);
    }, durationMs);
}

// ─── Navigation ───────────────────────────────────────────────────────────────

function navigateTo(page, params = {}) {
    const qs = Object.keys(params).length
        ? '?' + new URLSearchParams(params).toString()
        : '';
    window.location.href = `/pages/${page}${qs}`;
}

function getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

function goBack() {
    window.history.back();
}

// ─── Formatters ───────────────────────────────────────────────────────────────

function formatCurrency(value) {
    if (value === null || value === undefined) return '₹0.00';
    return '₹' + parseFloat(value).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function formatDate(dateString) {
    if (!dateString) return '—';
    const d = new Date(dateString);
    return d.toLocaleDateString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
}

function formatType(type) {
    const map = { 'CONE': 'Cone', 'SIZE_PATTERN': 'Size Pattern', 'OTHERS': 'Others' };
    return map[type] || type;
}

// ─── Confirmation Dialog ──────────────────────────────────────────────────────

function confirmAction(message) {
    return window.confirm(message);
}

// ─── Global Input Validation ──────────────────────────────────────────────────
document.addEventListener('keydown', function(e) {
    if (e.target && e.target.tagName === 'INPUT' && e.target.type === 'number') {
        if (['e', 'E', '+', '-'].includes(e.key)) {
            e.preventDefault();
        }
    }
});
