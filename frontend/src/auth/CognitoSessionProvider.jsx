import React from 'react';
import { SessionProvider } from '../context/SessionContext';

export const CognitoSessionProvider = ({ children }) => {
  return <SessionProvider>{children}</SessionProvider>;
};

export default CognitoSessionProvider;