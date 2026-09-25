export const AUTH_MODE = import.meta.env.VITE_AUTH_MODE || "cognito";

export const cognitoConfig = {
  domain: "andesstay-app.auth.us-east-1.amazoncognito.com",
  clientId: "28s01tgal6ti9qtv4vnroidreo",
  redirectUri: "http://localhost:5173/callback",
  responseType: "code",
  scope: "email openid profile",
};

export const isCognitoConfigured = () => {
  return Boolean(cognitoConfig.domain && cognitoConfig.clientId);
};

export const buildCognitoLoginUrl = () => {
  const { domain, clientId, redirectUri, responseType, scope } = cognitoConfig;
  const encodedRedirect = encodeURIComponent(redirectUri);
  const encodedScope = encodeURIComponent(scope);

  return `https://${domain}/oauth2/authorize?client_id=${clientId}&response_type=${responseType}&scope=${encodedScope}&redirect_uri=${encodedRedirect}`;
};

export default cognitoConfig;