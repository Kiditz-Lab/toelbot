// This is composible api

import { computed, type Ref } from 'vue';

export function useTextDiff(originalText: Ref<string>, changedText: Ref<string>) {
  /**
   * Membandingkan teks karakter demi karakter dan memberikan HTML yang menyoroti perbedaannya.
   */
  function getDiffHtml(oldText: string, newText: string) {
    let oldHtml = '';
    let newHtml = '';

    const maxLength = Math.max(oldText.length, newText.length);

    for (let i = 0; i < maxLength; i++) {
      const oldChar = oldText[i] || '';
      const newChar = newText[i] || '';

      if (oldChar !== newChar) {
        oldHtml += `<span class="diff-removed">${oldChar}</span>`;
        newHtml += `<span class="diff-added">${newChar}</span>`;
      } else {
        oldHtml += `<span class="diff-unchanged">${oldChar}</span>`;
        newHtml += `<span class="diff-unchanged">${newChar}</span>`;
      }
    }

    return { oldHtml, newHtml };
  }

  const diffResult = computed(() => {
    const { oldHtml, newHtml } = getDiffHtml(originalText.value, changedText.value);

    return {
      left: `<div class="diff-container">${oldHtml}</div>`,
      right: `<div class="diff-container">${newHtml}</div>`
    };
  });

  return diffResult;
}
