import { useTheme, type ThemeType } from '../ThemeContext';

export default function Settings() {
  const { theme, setTheme } = useTheme();

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-gray-800 dark:text-white">Settings</h1>
      
      <div className="bg-white dark:bg-gray-800 p-6 rounded shadow">
        <h2 className="text-xl font-semibold text-gray-800 dark:text-white mb-4">Theme Preferences</h2>
        <div className="space-y-4">
          {(['AUTO', 'LIGHT', 'DARK'] as ThemeType[]).map((t) => (
            <label key={t} className="flex items-center space-x-3 cursor-pointer">
              <input
                type="radio"
                name="theme"
                value={t}
                checked={theme === t}
                onChange={() => setTheme(t)}
                className="form-radio h-5 w-5 text-blue-600"
              />
              <span className="text-gray-700 dark:text-gray-300 capitalize">
                {t.toLowerCase()} {t === 'AUTO' && '(5 AM - 12 PM Light, otherwise Dark)'}
              </span>
            </label>
          ))}
        </div>
      </div>
    </div>
  );
}
