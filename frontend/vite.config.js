import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            "/api": {
                target: "http://localhost:8082",
                changeOrigin: true,
            },
            "/apiWithRequestParam": {
                target: "http://localhost:8082",
                changeOrigin: true,
            },
            "/apiWithHeader": {
                target: "http://localhost:8082",
                changeOrigin: true,
            },
        },
    },
});
