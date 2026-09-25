import React from "react";
import { buildCognitoLoginUrl } from "../auth/cognitoConfig";

const LOGIN_URL = buildCognitoLoginUrl();

export function Login() {
  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", fontFamily: "sans-serif", backgroundColor: "#f8fafc" }}>
      <div style={{ padding: "2.5rem", background: "white", border: "1px solid #e2e8f0", borderRadius: "12px", textAlign: "center", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)", maxWidth: "400px", width: "100%" }}>
        <div style={{ background: "#0d9488", color: "white", padding: "8px 12px", borderRadius: "6px", display: "inline-block", fontWeight: "bold", marginBottom: "1rem" }}>AS</div>
        <h2 style={{ margin: "0 0 0.5rem 0", color: "#1e293b" }}>AndesStay</h2>
        <p style={{ color: "#64748b", marginBottom: "1.5rem", fontSize: "14px" }}>Acceso corporativo para hostales, cabañas y lodges.</p>
        
        <a 
          href={LOGIN_URL}
          style={{
            display: "block",
            width: "100%",
            boxSizing: "border-box",
            padding: "12px 24px",
            background: "#0d9488",
            color: "white",
            border: "none",
            borderRadius: "6px",
            fontWeight: "bold",
            textDecoration: "none",
            boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
            textAlign: "center"
          }}
        >
          Iniciar sesión con Microsoft
        </a>
      </div>
    </div>
  );
}

export default Login;