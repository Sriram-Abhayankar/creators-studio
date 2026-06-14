// login.js — Login page logic
document.addEventListener('DOMContentLoaded', async () => {
    // If already logged in, go to dashboard
    if (getSession()) {
        window.location.href = '/pages/dashboard.html';
        return;
    }

    // Single-user guard: redirect to register if no account exists yet
    try {
        const statusRes = await apiCall('GET', '/api/auth/status');
        if (statusRes.data.registrationOpen) {
            window.location.href = '/pages/register.html';
            return;
        }
        // Account exists — hide the registration link entirely
        const authFooter = document.getElementById('auth-footer-link');
        if (authFooter) authFooter.style.display = 'none';
    } catch (ex) {
        // On status check failure, continue to show login normally
        console.warn('Could not check auth status:', ex.message);
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
