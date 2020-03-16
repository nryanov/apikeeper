import React, {FunctionComponent} from 'react'

const Header: FunctionComponent = () => (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <span className="navbar-brand">Apikeeper</span>
        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup"
                aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"/>
        </button>
    </nav>
);

export default Header;