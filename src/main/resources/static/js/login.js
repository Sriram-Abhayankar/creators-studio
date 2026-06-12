// login.js — Login page logic
document.addEventListener('DOMContentLoaded', () => {
    // If already logged in, go to dashboard
    if (getSession()) {
        window.location.href = '/pages/dashboard.html';
        return;
    }

    const form = document.getElementById('login-form');
    const btn  = document.getElementById('login-btn');
    const err  = document.getElementById('error-msg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        err.textContent = '';
        btn.disabled = true;
        btn.textContent = 'Logging in…';

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        try {
            const res = await apiCall('POST', '/api/auth/login', { username, password });
            saveSession(res.data);   // { id, username }
            window.location.href = '/pages/dashboard.html';
        } catch (ex) {
            err.textContent = ex.message;
            btn.disabled = false;
            btn.textContent = 'Login';
        }
    });
});
