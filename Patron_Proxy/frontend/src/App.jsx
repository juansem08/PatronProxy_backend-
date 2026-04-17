import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  BarChart, Activity, Clock, AlertTriangle, 
  RefreshCw, ChevronDown, ChevronUp, Database, 
  CreditCard, ShoppingCart 
} from 'lucide-react';
import { 
  LineChart, Line, XAxis, YAxis, CartesianGrid, 
  Tooltip, ResponsiveContainer, Legend 
} from 'recharts';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

const App = () => {
  const [metrics, setMetrics] = useState(null);
  const [logs, setLogs] = useState([]);
  const [expandedLog, setExpandedLog] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      const [metricsRes, logsRes] = await Promise.all([
        axios.get(`${API_BASE}/metrics/summary`),
        axios.get(`${API_BASE}/metrics/logs`)
      ]);
      setMetrics(metricsRes.data);
      setLogs(logsRes.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 3000); // Polling cada 3 segundos
    return () => clearInterval(interval);
  }, []);

  const simulateLoad = async () => {
    try {
      await axios.post(`${API_BASE}/metrics/simulate-load`);
      fetchData();
    } catch (error) {
      alert('Error al simular carga');
    }
  };

  const getServiceIcon = (name) => {
    switch (name) {
      case 'inventory': return <Database size={24} />;
      case 'orders': return <ShoppingCart size={24} />;
      case 'payments': return <CreditCard size={24} />;
      default: return <Activity size={24} />;
    }
  };

  if (loading && !metrics) {
    return <div className="loading">Cargando dashboard...</div>;
  }

  return (
    <div className="dashboard">
      <header>
        <div className="title-section">
          <h1>Dashboard de Observabilidad</h1>
          <p>Monitoreo de microservicios en tiempo real con Proxy de Auditoría</p>
        </div>
        <button className="btn-primary" onClick={simulateLoad}>
          <RefreshCw size={18} style={{marginRight: '8px', verticalAlign: 'middle'}} />
          Simular Carga
        </button>
      </header>

      <div className="metrics-grid">
        <div className="metric-card">
          <div className="metric-icon" style={{background: 'rgba(59, 130, 246, 0.2)', color: '#3b82f6'}}>
            <Activity />
          </div>
          <div className="metric-info">
            <h3>Total de Llamadas</h3>
            <div className="value">{metrics?.totalCalls}</div>
          </div>
        </div>

        <div className="metric-card">
          <div className="metric-icon" style={{background: 'rgba(245, 158, 11, 0.2)', color: '#f59e0b'}}>
            <AlertTriangle />
          </div>
          <div className="metric-info">
            <h3>Tasa de Éxito</h3>
            <div className="value">{((1 - (metrics?.errorRate || 0)) * 100).toFixed(1)}%</div>
          </div>
        </div>

        <div className="metric-card">
          <div className="metric-icon" style={{background: 'rgba(16, 185, 129, 0.2)', color: '#10b981'}}>
            <Clock />
          </div>
          <div className="metric-info">
            <h3>Tiempo Promedio</h3>
            <div className="value">{metrics?.avgResponseTime?.toFixed(2)}ms</div>
          </div>
        </div>
      </div>

      <div className="metrics-grid">
        {metrics?.services && Object.entries(metrics.services).map(([name, stats]) => (
          <div key={name} className={`metric-card ${stats.errorRate > 0.15 ? 'error-high' : ''}`}>
            <div className="metric-icon" style={{background: 'rgba(139, 92, 246, 0.2)', color: '#8b5cf6'}}>
              {getServiceIcon(name)}
            </div>
            <div className="metric-info">
              <h3 style={{color: '#fff'}}>{name}</h3>
              <div className="value" style={{fontSize: '1.2rem'}}>
                Err: {(stats.errorRate * 100).toFixed(1)}% | {stats.avgDuration.toFixed(0)}ms
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="main-content">
        <div className="chart-section" style={{gridColumn: 'span 2'}}>
          <div className="section-header">
            <h2>Tiempos de Respuesta (Últimas 20 llamadas)</h2>
          </div>
          <div style={{height: '300px', width: '100%'}}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={[...logs].reverse()}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="requestId" tick={false} stroke="#94a3b8" />
                <YAxis stroke="#94a3b8" />
                <Tooltip 
                  contentStyle={{backgroundColor: '#1e293b', border: '1px solid #334155'}}
                  itemStyle={{color: '#8b5cf6'}}
                />
                <Legend />
                <Line type="monotone" dataKey="durationMs" name="Duración (ms)" stroke="#8b5cf6" strokeWidth={3} dot={{r: 4}} activeDot={{r: 8}} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="logs-section">
          <div className="section-header">
            <h2>Logs en Tiempo Real</h2>
          </div>
          <table>
            <thead>
              <tr>
                <th>Servicio</th>
                <th>Operación</th>
                <th>Duración</th>
                <th>Estado</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {logs.map(log => (
                <React.Fragment key={log.id}>
                  <tr className="log-row" onClick={() => setExpandedLog(expandedLog === log.id ? null : log.id)}>
                    <td>{log.serviceId}</td>
                    <td>{log.operation}</td>
                    <td>{log.durationMs}ms</td>
                    <td>
                      <span className={`status-badge ${log.status === 'SUCCESS' ? 'status-success' : 'status-error'}`}>
                        {log.status}
                      </span>
                    </td>
                    <td>{expandedLog === log.id ? <ChevronUp size={16}/> : <ChevronDown size={16}/>}</td>
                  </tr>
                  {expandedLog === log.id && (
                    <tr>
                      <td colSpan="5">
                        <div className="details-panel">
                          <div><strong>Request ID:</strong> {log.requestId}</div>
                          <div><strong>Timestamp:</strong> {log.timestamp}</div>
                          <div><strong>Input Params:</strong> {log.inputParams}</div>
                          <div><strong>Response:</strong> {log.response}</div>
                          {log.stackTrace && (
                            <div style={{color: '#ef4444', marginTop: '10px'}}>
                              <strong>Stack Trace:</strong> {log.stackTrace.substring(0, 300)}...
                            </div>
                          )}
                        </div>
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default App;
