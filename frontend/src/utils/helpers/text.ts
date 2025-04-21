const capitalize = (str: string) => {
  if (!str) return '';
  const spaced = str.replace(/([a-z])([A-Z])/g, '$1 $2'); // Add space before uppercase
  return spaced.charAt(0).toUpperCase() + spaced.slice(1);
};

export { capitalize };
