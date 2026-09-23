import React from 'react';
import { Routes, Route, Navigate, Link } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { ThemeProvider } from './ThemeContext';

import Login from './pages/Login';
import Register from './pages/Register';
import OtpVerification from './pages/OtpVerification';
import Dashboard from './pages/Dashboard';
import Security from './pages/Security';
import Settings from './pages/Settings';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function Layout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
      {isAuthenticated && (
        <nav className="bg-white dark:bg-gray-800 shadow p-4">
          <div className="max-w-6xl mx-auto flex justify-between items-center">
            <div className="font-bold text-xl text-blue-600 dark:text-blue-400">
              SecureAdaptive
            </div>
            <div className="space-x-4 flex items-center">
              <Link to="/dashboard" className="text-gray-700 dark:text-gray-300 hover:text-blue-500">Dashboard</Link>
              <Link to="/security" className="text-gray-700 dark:text-gray-300 hover:text-blue-500">Security</Link>
              <Link to="/settings" className="text-gray-700 dark:text-gray-300 hover:text-blue-500">Settings</Link>
              <button 
                onClick={logout}
                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
              >
                Logout
              </button>
            </div>
          </div>
        </nav>
      )}
      <main className="p-4">
        {children}
      </main>
    </div>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <Layout>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/verify-otp" element={<OtpVerification />} />

          {/* Protected Routes */}
          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/security" element={<ProtectedRoute><Security /></ProtectedRoute>} />
          <Route path="/settings" element={<ProtectedRoute><Settings /></ProtectedRoute>} />
          
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Layout>
    </ThemeProvider>
  );
}
