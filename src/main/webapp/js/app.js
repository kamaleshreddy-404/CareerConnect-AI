/**
 * CareerConnect AI - Core Global Application Script
 * Theme Toggling, Instant Background Consistency, Toast Notifications, Modal Control
 */

// Execute theme check IMMEDIATELY to prevent background flash on page/section transitions
(function applyInstantTheme() {
  const savedTheme = localStorage.getItem('careerconnect_theme') || 'light';
  document.documentElement.setAttribute('data-theme', savedTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initToastFromUrl();
});

// Theme Management
function initTheme() {
  const savedTheme = localStorage.getItem('careerconnect_theme') || 'light';
  document.documentElement.setAttribute('data-theme', savedTheme);
  updateThemeIcon(savedTheme);
}

function toggleTheme() {
  const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
  const newTheme = currentTheme === 'light' ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', newTheme);
  localStorage.setItem('careerconnect_theme', newTheme);
  updateThemeIcon(newTheme);
}

function updateThemeIcon(theme) {
  const themeToggles = document.querySelectorAll('.theme-toggle');
  themeToggles.forEach(toggle => {
    toggle.innerHTML = theme === 'dark' 
      ? '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>'
      : '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>';
  });
}

// Toast Notifications
function showToast(message, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
    <span>${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function initToastFromUrl() {
  const params = new URLSearchParams(window.location.search);
  const msg = params.get('msg');
  if (msg) {
    const messages = {
      'registered': 'Registration successful! Please login to continue.',
      'recruiter_registered': 'Recruiter account registered! Login to access your portal.',
      'logged_out': 'You have been logged out successfully.',
      'password_reset': 'Password reset successful! You can now log in.',
      'applied_success': 'Job Application submitted successfully!',
      'job_posted': 'Job posting created successfully!',
      'job_updated': 'Job posting updated.',
      'job_deleted': 'Job posting removed.',
      'profile_updated': 'Profile updated successfully!',
      'status_updated': 'Application status updated.',
      'company_updated': 'Company details updated.',
      'recruiter_updated': 'Recruiter status updated.',
      'job_flagged': 'Job status updated.',
      'category_added': 'Job category added.',
      'category_deleted': 'Job category removed.',
      'already_applied': 'You have already applied for this job.'
    };
    if (messages[msg]) {
      showToast(messages[msg], 'success');
    }
  }
}

// Modal Control
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}
