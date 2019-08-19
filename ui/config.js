const env = process.env.NODE_ENV; // 'dev' or 'test'

const development = {
  api: {
    host: process.env.DEV_API_HOST || 'localhost',
    port: parseInt(process.env.DEV_DB_PORT) || 8080
  }
};
const production = {
  api: {
    host: process.env.DEV_API_HOST || 'localhost',
    port: parseInt(process.env.DEV_API_PORT) || 8080
  }
};

const config = {
  development,
  production
};

module.exports = config[env];
