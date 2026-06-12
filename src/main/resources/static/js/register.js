// register.js — Registration page logic
document.addEventListener('DOMContentLoaded', () => {
    if (getSession()) {
        window.location.href = '/pages/dashboard.html';
        return;
    }

    const form = document.getElementById('register-form');
    const btn  = document.getElementById('register-btn');
    const err  = document.getElementById('error-msg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        err.textContent = '';

        const username        = document.getElementById('username').value.trim();
        const password        = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        if (password !== confirmPassword) {
            err.textContent = 'Passwords do not match.';
            return;
        }

        btn.disabled = true;
        btn.textContent = 'Registering…';

        try {
            await apiCall('POST', '/api/auth/register', { username, password, confirmPassword });
            showToast('Account created! Please log in.', 'success');
            setTimeout(() => { window.location.href = '/pages/login.html'; }, 1200);
        } catch (ex) {
            err.textContent = ex.message;
            btn.disabled = false;
            btn.textContent = 'Register';
        }
    });
});
