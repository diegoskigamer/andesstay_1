import React, { createContext, useContext, useState, useEffect } from "react";
import { parseJwt, extractRoleFromToken } from "../auth/jwt";
import { getStoredTokens, saveTokens, clearTokens } from "../auth/cognitoTokenStorage";

export const ROLES = {
  ADMIN: "Admin",
  RECEPCIONISTA: "Recepcionista",
  HUESPED: "Huesped",
  AUDITOR: "Auditor",
};

export const AZURE_APP_ROLE_TO_ROLE = {
  Admin: ROLES.ADMIN,
  Recepcionista: ROLES.RECEPCIONISTA,
  Operador: ROLES.RECEPCIONISTA,
  Huesped: ROLES.HUESPED,
  Cliente: ROLES.HUESPED,
  Auditor: ROLES.AUDITOR,
};

export const SessionContext = createContext(null);

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error("useSession debe usarse dentro de un SessionProvider");
  return ctx;
}

export function SessionProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const mapRole = (rawRole) => {
    if (!rawRole) return "Sin rol asignado";
    return AZURE_APP_ROLE_TO_ROLE[rawRole] || rawRole;
  };

  useEffect(() => {
    try {
      const { idToken } = getStoredTokens();
      if (idToken) {
        const payload = parseJwt(idToken);
        const extractedRole = extractRoleFromToken(idToken);
        const mappedRole = mapRole(extractedRole);

        if (payload) {
          setUser({
            email: payload.email || payload.username || payload['cognito:username'] || '',
            role: mappedRole,
            rawClaims: payload,
          });
        }
      }
    } catch (error) {
      console.error("Error al cargar la sesión:", error);
    } finally {
      setLoading(false);
    }
  }, []);

  const loginSession = (tokens) => {
    if (!tokens) return;
    
    // Estandarizamos las claves para garantizar que se guarden correctamente
    const tokensToSave = {
      idToken: tokens.idToken || tokens.id_token,
      accessToken: tokens.accessToken || tokens.access_token,
      refreshToken: tokens.refreshToken || tokens.refresh_token,
    };
    
    saveTokens(tokensToSave);

    const activeIdToken = tokensToSave.idToken;
    const payload = parseJwt(activeIdToken);
    const extractedRole = extractRoleFromToken(activeIdToken);
    const mappedRole = mapRole(extractedRole);

    setUser({
      email: payload?.email || payload?.username || payload?.['cognito:username'] || '',
      role: mappedRole,
      rawClaims: payload,
    });
  };

  const logoutSession = () => {
    clearTokens();
    setUser(null);
  };

  // Mapeamos alias 'login' y 'logout' por si Login.jsx los llama con ese nombre
  const value = {
    user,
    loading,
    loginSession,
    login: loginSession,
    logoutSession,
    logout: logoutSession,
    setUser,
  };

  return (
    <SessionContext.Provider value={value}>
      {!loading && children}
    </SessionContext.Provider>
  );
}

export default SessionContext;