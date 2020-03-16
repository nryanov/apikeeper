import React from 'react';
import Header from "./components/Header";
import EntityList from "./components/EntityList";
import Search from "./components/Search";

function App() {
  return (
    <div className="container-fluid">
      <Header/>

      <div className="row mt-4">
          <div className="col-4">
              <Search/>
              <EntityList/>
          </div>

          <div className="col-8">

          </div>
      </div>
    </div>
  );
}

export default App;
