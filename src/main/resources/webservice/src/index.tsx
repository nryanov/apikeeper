import React from 'react';
import ReactDOM from 'react-dom';
import 'jquery/dist/jquery.min'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.min.js';
import Application from "./components/Application";
import { Provider } from 'react-redux'
import store from "./logic/store";


ReactDOM.render(
    <Provider store={store}>
        <Application />
    </Provider>,
    document.getElementById('root')
);

