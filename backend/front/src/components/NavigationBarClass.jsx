import React from "react";
import {Navbar, Nav} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom"

class NavigationBarClass extends React.Component {

    constructor(props) {
        super(props);
        this.goHome = this.goHome.bind(this);
    }

    // Метод, который осуществляет переход на другую страницу (Another Home)
    goHome() {
        this.props.navigate('Another_Home');
    }

    render() {
        return (
          <Navbar bg="light" expand="lg">
              <Navbar.Brand><FontAwesomeIcon icon={faHome}/>{' '} My RPO</Navbar.Brand>
              <Navbar.Toggle aria-controls="basic-navbar-nav"/>
              <Navbar.Collapse id="basics-navbar-nav">
                  <Nav className="me-auto">
                      {/* Переход может осуществляться как напрямую */}
                      <Nav.Link href="/home">Home</Nav.Link>

                      {/* Переход может осуществляться так и при помощи функции*/}
                      <Nav.Link onClick={this.goHome}>Another Home</Nav.Link>
                  </Nav>
              </Navbar.Collapse>
          </Navbar>
        );
    }
}

// Свойство, которое получает компонент для навигации и передаёт экземпляру класса
const NavigationBar = props => {
    const navigate = useNavigate()
    return <NavigationBarClass navigate={navigate} {...props} />
}

export default NavigationBar;