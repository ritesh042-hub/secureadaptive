import { useEffect, useState } from 'react';
import api from '../api';
import { Trash2, Smartphone, Shield, Clock } from 'lucide-react';

export default function Security() {
  const [data, setData] = useState({
    activeSessions: [],
    trustedDevices: [],
    loginHistory: []
  });

  const fetchSecurityData = async () => {
    try {
      const res = await api.get('/api/security/dashboard');
      setData(res.data);
    } catch (err) {
      console.error('Failed to fetch security data', err);
    }
  };

  useEffect(() => {
    fetchSecurityData();
  }, []);

  const revokeDevice = async (id: string) => {
    try {
      await api.delete(`/api/security/trusted-device/${id}`);
      fetchSecurityData();
    } catch (err) {
      console.error('Failed to revoke device', err);
    }
  };

  const revokeSession = async (id: string) => {
    try {
      await api.delete(`/api/security/session/${id}`);
      fetchSecurityData();
    } catch (err) {
      console.error('Failed to revoke session', err);
    }
  };

  return (
    <div className="max-w-5xl mx-auto p-6 space-y-8">
      <h1 className="text-3xl font-bold text-gray-800 dark:text-white mb-6">Security Dashboard</h1>

      {/* Active Sessions */}
      <div className="bg-white dark:bg-gray-800 rounded shadow p-6">
        <h2 className="text-xl font-semibold text-gray-800 dark:text-white mb-4 flex items-center">
          <Smartphone className="mr-2 h-5 w-5 text-blue-500" /> Active Sessions
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b dark:border-gray-700 text-gray-600 dark:text-gray-400">
                <th className="py-2">Device/IP</th>
                <th className="py-2">Last Active</th>
                <th className="py-2">Action</th>
              </tr>
            </thead>
            <tbody>
              {data.activeSessions?.map((session: any) => (
                <tr key={session.id} className="border-b dark:border-gray-700 last:border-0 text-gray-800 dark:text-gray-200">
                  <td className="py-3">{session.deviceInfo || session.ipAddress} {session.isCurrent && <span className="ml-2 text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Current</span>}</td>
                  <td className="py-3">{new Date(session.lastActive).toLocaleString()}</td>
                  <td className="py-3">
                    {!session.isCurrent && (
                      <button onClick={() => revokeSession(session.id)} className="text-red-500 hover:text-red-700 flex items-center text-sm">
                        <Trash2 className="w-4 h-4 mr-1" /> Revoke
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {(!data.activeSessions || data.activeSessions.length === 0) && (
                <tr><td colSpan={3} className="py-4 text-gray-500 text-center">No active sessions</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Trusted Devices */}
      <div className="bg-white dark:bg-gray-800 rounded shadow p-6">
        <h2 className="text-xl font-semibold text-gray-800 dark:text-white mb-4 flex items-center">
          <Shield className="mr-2 h-5 w-5 text-green-500" /> Trusted Devices
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b dark:border-gray-700 text-gray-600 dark:text-gray-400">
                <th className="py-2">Device Name</th>
                <th className="py-2">Trusted On</th>
                <th className="py-2">Action</th>
              </tr>
            </thead>
            <tbody>
              {data.trustedDevices?.map((device: any) => (
                <tr key={device.id} className="border-b dark:border-gray-700 last:border-0 text-gray-800 dark:text-gray-200">
                  <td className="py-3">{device.deviceName}</td>
                  <td className="py-3">{new Date(device.trustedAt).toLocaleString()}</td>
                  <td className="py-3">
                    <button onClick={() => revokeDevice(device.id)} className="text-red-500 hover:text-red-700 flex items-center text-sm">
                      <Trash2 className="w-4 h-4 mr-1" /> Revoke
                    </button>
                  </td>
                </tr>
              ))}
              {(!data.trustedDevices || data.trustedDevices.length === 0) && (
                <tr><td colSpan={3} className="py-4 text-gray-500 text-center">No trusted devices</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Login History */}
      <div className="bg-white dark:bg-gray-800 rounded shadow p-6">
        <h2 className="text-xl font-semibold text-gray-800 dark:text-white mb-2 flex items-center">
          <Clock className="mr-2 h-5 w-5 text-purple-500" /> Login History
        </h2>
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
          Location data is an approximation based on public IP address and is not GPS-accurate.
        </p>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[800px]">
            <thead>
              <tr className="border-b dark:border-gray-700 text-gray-600 dark:text-gray-400">
                <th className="py-2">Time</th>
                <th className="py-2">IP Address</th>
                <th className="py-2">Location</th>
                <th className="py-2">Coordinates</th>
                <th className="py-2">Status</th>
              </tr>
            </thead>
            <tbody>
              {data.loginHistory?.map((history: any) => {
                // Determine display location
                const locParts = [history.city, history.state, history.country].filter(Boolean);
                const locationDisplay = locParts.length > 0 ? locParts.join(', ') : 'Unknown';
                
                // Determine coordinates
                const coordDisplay = (history.latitude && history.longitude)
                  ? `${history.latitude}, ${history.longitude}`
                  : 'Coordinates unavailable';

                return (
                  <tr key={history.id} className="border-b dark:border-gray-700 last:border-0 text-gray-800 dark:text-gray-200">
                    <td className="py-3 pr-4 whitespace-nowrap">{new Date(history.loginTimestamp).toLocaleString()}</td>
                    <td className="py-3 pr-4">{history.ipAddress}</td>
                    <td className="py-3 pr-4">{locationDisplay}</td>
                    <td className="py-3 pr-4 text-xs text-gray-500">{coordDisplay}</td>
                    <td className="py-3">
                      <span className={`px-2 py-1 rounded text-xs ${history.loginStatus === 'SUCCESS' ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'}`}>
                        {history.loginStatus}
                      </span>
                    </td>
                  </tr>
                );
              })}
              {(!data.loginHistory || data.loginHistory.length === 0) && (
                <tr><td colSpan={5} className="py-4 text-gray-500 text-center">No login history</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
