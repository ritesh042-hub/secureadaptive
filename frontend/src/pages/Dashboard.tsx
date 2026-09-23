import { useEffect, useState } from 'react';
import api from '../api';

export default function Dashboard() {
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get('/api/user/me');
        setUser(res.data);
      } catch (err) {
        console.error('Failed to fetch user', err);
      }
    };
    fetchUser();
  }, []);

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-4 text-gray-800 dark:text-white">Dashboard</h1>
      {user ? (
        <div className="bg-white dark:bg-gray-800 p-6 rounded shadow">
          <h2 className="text-xl text-gray-700 dark:text-gray-300">Welcome back, {user.name}!</h2>
          <p className="text-gray-600 dark:text-gray-400 mt-2">Email: {user.email}</p>
          <p className="text-gray-600 dark:text-gray-400">Phone: {user.phone}</p>
        </div>
      ) : (
        <p className="text-gray-600 dark:text-gray-400">Loading user info...</p>
      )}
    </div>
  );
}
