import { useEffect, useState } from "react";

function App() {
  const [incidents, setIncidents] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8082/api/incidents")
      .then(res => res.json())
      .then(data => setIncidents(data));
  }, []);

  const openCount = incidents.filter(i => i.status === "OPEN").length;
  const resolvedCount = incidents.filter(i => i.status === "RESOLVED").length;

  return (
    <div style={styles.container}>
      <h2 style={styles.title}> Incident Dashboard</h2>

      {/* KPI CARDS */}
      <div style={styles.kpiContainer}>
        <div style={{ ...styles.kpiCard, borderLeft: "5px solid red" }}>
          <h3>Open</h3>
          <p>{openCount}</p>
        </div>
        <div style={{ ...styles.kpiCard, borderLeft: "5px solid green" }}>
          <h3>Resolved</h3>
          <p>{resolvedCount}</p>
        </div>
      </div>

      <div style={styles.main}>

        {/* LEFT: INCIDENT LIST */}
        <div style={styles.left}>
          <h3>Live Feed</h3>

          {incidents.map(i => (
            <div
              key={i.id}
              onClick={() => setSelected(i)}
              style={{
                ...styles.card,
                borderLeft: `5px solid ${getSeverityColor(i.componentId)}`
              }}
            >
              <strong>{i.componentId}</strong>
              <p>Status: {i.status}</p>
              <p>Severity: {getSeverity(i.componentId)}</p>
            </div>
          ))}
        </div>

        {/* RIGHT: DETAILS */}
        <div style={styles.right}>
          {selected && (
            <>
              <h3>Incident Detail</h3>

              <div style={styles.detailCard}>
                <p><b>ID:</b> {selected.id}</p>
                <p><b>Status:</b> {selected.status}</p>
                <p><b>RCA:</b> {selected.rca || "Not filled"}</p>
                <p><b>MTTR:</b> {selected.mttr || "-"} ms</p>
                <p><b>Created:</b> {formatDate(selected.createdTime)}</p>
                <p><b>Resolved:</b> {formatDate(selected.resolvedTime)}</p>
              </div>

              <RCAForm incident={selected} />
            </>
          )}
        </div>

      </div>
    </div>
  );
}

//  RCA FORM
function RCAForm({ incident }) {
  const [rca, setRca] = useState("");
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [fix, setFix] = useState("");

  const submitRCA = async () => {
    await fetch(`http://localhost:8082/api/rca/${incident.id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        rca,
        startTime: new Date(start).getTime(),
        endTime: new Date(end).getTime(),
        fix
      })
    });

    alert("RCA Submitted ✅");
    window.location.reload();
  };

  return (
    <div style={styles.form}>
      <h4>RCA Form</h4>

      <input type="datetime-local" onChange={e => setStart(e.target.value)} />
      <input type="datetime-local" onChange={e => setEnd(e.target.value)} />

      <select onChange={e => setRca(e.target.value)}>
        <option>Select Root Cause</option>
        <option>Infra Issue</option>
        <option>Code Bug</option>
        <option>Network</option>
      </select>

      <textarea
        placeholder="Fix Applied"
        onChange={e => setFix(e.target.value)}
      />

      <button onClick={submitRCA}>Submit RCA</button>
    </div>
  );
}

//  UTILS

function getSeverity(component) {
  if (component.includes("DB")) return "P0";
  if (component.includes("CACHE")) return "P2";
  return "P3";
}

function getSeverityColor(component) {
  if (component.includes("DB")) return "red";
  if (component.includes("CACHE")) return "orange";
  return "yellow";
}

function formatDate(time) {
  if (!time) return "-";
  return new Date(time).toLocaleString("en-IN");
}

//  STYLES

const styles = {
  container: {
    background: "#0d1117",
    color: "white",
    minHeight: "100vh",
    padding: 20,
    fontFamily: "Arial"
  },
  title: {
    marginBottom: 20
  },
  kpiContainer: {
    display: "flex",
    gap: 20,
    marginBottom: 20
  },
  kpiCard: {
    background: "#161b22",
    padding: 20,
    borderRadius: 10,
    width: 150
  },
  main: {
    display: "flex",
    gap: 20
  },
  left: {
    width: "40%"
  },
  right: {
    width: "60%"
  },
  card: {
    background: "#161b22",
    padding: 15,
    margin: 10,
    borderRadius: 10,
    cursor: "pointer"
  },
  detailCard: {
    background: "#161b22",
    padding: 20,
    borderRadius: 10,
    marginBottom: 20
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: 10,
    background: "#161b22",
    padding: 20,
    borderRadius: 10
  }
};

export default App;