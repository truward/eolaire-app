var React = require('React');
var Router = require('director').Router;
var DEFAULT_LIMIT = require("../service/ajax-eolaire-service.js").DEFAULT_LIMIT;

var ItemListPage = require('./item/item-list-page.js');
var AboutPage = require('./about/about-page.js');

var Nav = {
  UNDEFINED: "undefined",

  ITEM_LIST: "items",

  ABOUT: "about"
};

function setStartTitle(pageNamePart) {
  if (pageNamePart != null) {
    document.title = "Eolaire \u00BB " + pageNamePart;
    return;
  }

  document.title = "Eolaire \u00BB Loading...";
}

var TitleService = {
  setTitle: setStartTitle
}

module.exports = React.createClass({
  getInitialState: function () {
    var services = { titleService: TitleService };
    for (var serviceKey in this.props.services) {
      services[serviceKey] = this.props.services[serviceKey];
    }

    return {
      services: services,

      nowShowing: Nav.UNDEFINED, // current widget

      // controller variables
      // ...
    };
  },

  componentDidMount: function () {
    var gotoItemsPage = this.setState.bind(this, {nowShowing: Nav.ITEM_LIST});

    var gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT});

    var router = Router({
      '/items': gotoItemsPage,
      '/about': gotoAboutPage
    });

    router.init('/items');
  },

  render: function() {
    switch (this.state.nowShowing) {
      case Nav.UNDEFINED: // happens once on loading
        setStartTitle("Main");
        return (<div/>);

      case Nav.Items:
        setStartTitle("Items");
        return (<ItemListPage services={this.state.services}/>);

      case Nav.ABOUT:
        setStartTitle("About");
        return (<AboutPage />);

      default:
        setStartTitle();
        return (<ItemListPage services={this.state.services}/>);
    }
  }
});

