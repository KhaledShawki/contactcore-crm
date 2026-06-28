// Copyright (c) Khaled Shawki. All rights reserved.

import React from 'react';
import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { store } from './store/store';
import ThemeBootstrap from './theme/ThemeBootstrap';
import { NotificationProvider } from './notifications/NotificationProvider';
import './styles.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <Provider store={store}>
      <NotificationProvider>
        <ThemeBootstrap>
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </ThemeBootstrap>
      </NotificationProvider>
    </Provider>
  </React.StrictMode>,
);
