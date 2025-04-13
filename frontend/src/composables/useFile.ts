import pdf from '@/assets/images/files/pdf.png';
import doc from '@/assets/images/files/doc.png';
import docx from '@/assets/images/files/docx.png';
import txt from '@/assets/images/files/txt.png';
export function useFile() {
  const formatFileSize = (size: number | null | undefined) => {
    if (!size || size <= 0) return '0 B';
  
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(2)} KB`;
    if (size < 1024 * 1024 * 1024) return `${(size / (1024 * 1024)).toFixed(2)} MB`;
    return `${(size / (1024 * 1024 * 1024)).toFixed(2)} GB`;
  };
  
  const getFileIcon = (filename:string) => {
    const extension = filename.split('.').pop()?.toLowerCase() || 'default';
    switch (extension) {
      case 'pdf':
        return pdf;
      case 'doc':
        return doc;
      case 'docx':
        return docx;
      case 'txt':
        return txt;
      default:
        return txt;
    }
  };
  return { formatFileSize, getFileIcon };
  
}
