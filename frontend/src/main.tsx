import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ClerkProvider } from '@clerk/react'
import App, { AuthenticatedApp } from './App'
import './index.css'

const publishableKey = import.meta.env.VITE_CLERK_PUBLISHABLE_KEY?.trim()
const root = createRoot(document.getElementById('root')!)

root.render(
  <StrictMode>
    {publishableKey ? (
      <ClerkProvider publishableKey={publishableKey}>
        <AuthenticatedApp />
      </ClerkProvider>
    ) : (
      <App />
    )}
  </StrictMode>,
)
