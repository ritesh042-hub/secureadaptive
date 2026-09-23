import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import api from './api';
import { useAuth } from './AuthContext';

export type ThemeType = 'AUTO' | 'LIGHT' | 'DARK';

interface ThemeContextType {
  theme: ThemeType;
  setTheme: (theme: ThemeType) => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function ThemeProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  
  const [theme, setThemeState] = useState<ThemeType>(() => {
    return (localStorage.getItem('themePref') as ThemeType) || 'AUTO';
  });

  useEffect(() => {
    const fetchTheme = async () => {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          const res = await api.get('/api/user/me');
          if (res.data.themePreference) {
            setThemeState(res.data.themePreference);
            localStorage.setItem('themePref', res.data.themePreference);
          }
        }
      } catch (e) {
        console.error('Failed to fetch user theme', e);
      }
    };
    
    fetchTheme();
  }, [isAuthenticated]);

  const setTheme = async (newTheme: ThemeType) => {
    setThemeState(newTheme);
    localStorage.setItem('themePref', newTheme);
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await api.put('/api/user/theme', { theme: newTheme });
      }
    } catch (e) {
      console.error('Failed to update theme on server', e);
    }
  };

  useEffect(() => {
    const applyTheme = () => {
      if (theme === 'LIGHT') {
        document.documentElement.classList.remove('dark');
      } else if (theme === 'DARK') {
        document.documentElement.classList.add('dark');
      } else {
        // AUTO
        const hourStr = new Intl.DateTimeFormat('en-US', {
          timeZone: 'Asia/Kolkata',
          hour: 'numeric',
          hour12: false
        }).format(new Date());
        
        // hourStr might be "24" for midnight in some Node versions, so safely parse
        let hours = parseInt(hourStr, 10);
        if (hours === 24) hours = 0;
        
        // 5:00 AM to 12:00 PM (11:59 AM) is LIGHT
        // 12:00 PM to 4:59 AM is DARK
        if (hours >= 5 && hours < 12) {
          document.documentElement.classList.remove('dark');
        } else {
          document.documentElement.classList.add('dark');
        }
      }
    };

    applyTheme();
    // Re-check every minute for AUTO theme
    const interval = setInterval(applyTheme, 60000);
    return () => clearInterval(interval);
  }, [theme]);

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}
