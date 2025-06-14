import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
//import fs from 'fs';

// https://vite.dev/config/
export default defineConfig({
  assetsInclude: ['**/*.ttf'],
  server: {
    host: '0.0.0.0',
    port: 80,
    // https: {
    //   key: fs.readFileSync('./certs/localhost-key.pem'),
    //   cert: fs.readFileSync('./certs/localhost.pem'),
    // }
  },
  plugins: [react()],
})
