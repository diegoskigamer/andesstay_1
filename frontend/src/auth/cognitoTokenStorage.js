const ID_TOKEN_KEY = 'id_token';
const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

// Guardar todos los tokens recibidos
export const saveTokens = (tokens) => {
  if (!tokens) return;
  if (tokens.id_token || tokens.idToken) {
    localStorage.setItem(ID_TOKEN_KEY, tokens.id_token || tokens.idToken);
  }
  if (tokens.access_token || tokens.accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, tokens.access_token || tokens.accessToken);
  }
  if (tokens.refresh_token || tokens.refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refresh_token || tokens.refreshToken);
  }
};

// Función que requiere client.js para leer los tokens
export const getStoredTokens = () => {
  return {
    idToken: localStorage.getItem(ID_TOKEN_KEY),
    accessToken: localStorage.getItem(ACCESS_TOKEN_KEY),
    refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY),
  };
};

export const getIdToken = () => localStorage.getItem(ID_TOKEN_KEY);
export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY);
export const getRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

export const clearTokens = () => {
  localStorage.removeItem(ID_TOKEN_KEY);
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};