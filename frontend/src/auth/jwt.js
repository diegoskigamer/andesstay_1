export function parseJwt(token) {
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Error al decodificar JWT", e);
    return null;
  }
}

export function extractRoleFromToken(token) {
  const payload = parseJwt(token);
  if (!payload) return null;

  let rawRole = payload['custom:role'] || payload['roles'] || payload['cognito:groups'];
  if (!rawRole) return null;

  if (Array.isArray(rawRole) && rawRole.length > 0) {
    rawRole = rawRole[0];
  }

  if (typeof rawRole === 'string') {
    return rawRole.replace(/[\[\]"\\]/g, '').trim();
  }

  return String(rawRole);
}