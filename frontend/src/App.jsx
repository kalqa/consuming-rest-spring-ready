import { useState } from "react";

function App() {
    const [result, setResult] = useState(null);

    const fetchApi = async (url, options = {}) => {
        try {
            const res = await fetch(url, options);
            setResult(await res.json());
        } catch (e) {
            setResult({ error: e.message });
        }
    };

    return (
        <div style={{ padding: "2rem" }}>
            <h1>Quotes API (React + Vite)</h1>

            <div style={{ display: "flex", flexWrap: "wrap", gap: "0.5rem" }}>
                <button onClick={() => fetchApi("/api")}>GET all</button>
                <button onClick={() => fetchApi("/api/1")}>GET one (id=1)</button>
                <button onClick={() => fetchApi("/api/random")}>GET random</button>
                <button onClick={() => fetchApi("/apiWithRequestParam?id=11")}>
                    GET with param (id=11)
                </button>
                <button
                    onClick={() =>
                        fetchApi("/apiWithHeader", {
                            headers: { requestId: "react-client-123" },
                        })
                    }
                >
                    GET with header
                </button>
                <button
                    onClick={() =>
                        fetchApi("/api/quote", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({
                                type: "success",
                                value: { id: null, quote: "added from React" },
                            }),
                        })
                    }
                >
                    POST add quote
                </button>
                <button
                    onClick={() =>
                        fetchApi("/api/quote/12", {
                            method: "DELETE",
                        })
                    }
                >
                    DELETE quote (id=12)
                </button>
            </div>

            <pre style={{ marginTop: "1rem" }}>
        {result ? JSON.stringify(result, null, 2) : "No data yet..."}
      </pre>
        </div>
    );
}

export default App;
