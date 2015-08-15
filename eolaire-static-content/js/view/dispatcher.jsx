var React = require('React');
var Router = require('director').Router;

// widgets

var EntityListPage = require('./entity/entity-list-page.js');
var ItemByTypeListPage = require('./item/item-by-type-list.js');
var AboutPage = require('./about/about-page.js');

// navigation IDs

var Nav = {
  UNDEFINED: "UNDEFINED",

  ITEM_BY_TYPE_LIST: "ITEM_BY_TYPE_LIST",

  ENTITY_LIST: "ENTITY_LIST",

  ABOUT: "ABOUT"
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

      type: undefined,
      offsetToken: undefined,
      limit: undefined
    };
  },

  componentDidMount: function () {
    var gotoEntitiesPage = this.setState.bind(this, {nowShowing: Nav.ENTITY_LIST});

    var gotoItemByTypePage = function (type, limit) {
      this.setState({
        nowShowing: Nav.ITEM_BY_TYPE_LIST,
        type: parseInt(type),
        limit: parseInt(limit)
      });
    }.bind(this);

    var gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT});

    var router = Router({
      '/entities': gotoEntitiesPage,
      '/item/type/:type/limit/:limit': gotoItemByTypePage,
      '/about': gotoAboutPage
    });

    router.init('/entities');
  },

  render: function() {
    switch (this.state.nowShowing) {
      case Nav.UNDEFINED: // happens once on loading
        setStartTitle("Main");
        return (<div/>);

      case Nav.ENTITY_LIST:
        setStartTitle("Entities");
        return (<EntityListPage services={this.state.services}/>);

      case Nav.ITEM_BY_TYPE_LIST:
        setStartTitle("Items");
        return (<ItemByTypeListPage
                  services={this.state.services}
                  type={this.state.type}
                  offsetToken={this.state.offsetToken}
                  limit={this.state.limit}
                  />);

      case Nav.ABOUT:
        setStartTitle("About");
        return (<AboutPage />);

      default:
        setStartTitle();
        return (<EntityListPage services={this.state.services}/>);
    }
  }
});

