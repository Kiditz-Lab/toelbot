// File: public/embed.js

(function () {
  const script = document.currentScript;
  const id = script.getAttribute('data-id');
  const icon = script.getAttribute('data-icon') || 'https://toelbox.com/logo.svg';
  const width = script.getAttribute('data-width') || '400px';
  const height = script.getAttribute('data-height') || '600px';

  if (!id) {
    console.error('embed.js error: data-id is required.');
    return;
  }

  const container = document.createElement('div');
  container.style.position = 'fixed';
  container.style.bottom = '20px';
  container.style.right = '20px';
  container.style.zIndex = '9999';

  const button = document.createElement('img');
  button.src = icon;
  button.style.width = '60px';
  button.style.height = '60px';
  button.style.borderRadius = '50%';
  button.style.cursor = 'pointer';
  button.style.boxShadow = '0 4px 12px rgba(0,0,0,0.2)';
  button.style.transition = 'transform 0.3s ease';

  const iframe = document.createElement('iframe');
  iframe.src = `https://toelbox.com/embed/${id}`;
  iframe.style.width = width;
  iframe.style.height = height;
  iframe.style.border = 'none';
  iframe.style.position = 'absolute';
  iframe.style.bottom = '80px';
  iframe.style.right = '0';
  iframe.style.borderRadius = '12px';
  iframe.style.boxShadow = '0 4px 24px rgba(0,0,0,0.3)';
  iframe.style.display = 'none';

  button.onclick = () => {
    iframe.style.display = iframe.style.display === 'none' ? 'block' : 'none';
  };

  container.appendChild(button);
  container.appendChild(iframe);
  document.body.appendChild(container);
})();
