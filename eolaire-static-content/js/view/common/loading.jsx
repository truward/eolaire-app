var React = require('React');

module.exports = React.createClass({
  render: function() {
    var msg = "Loading " + this.props.target + "...";
    return (
      <div className="container">
        <p>{msg}</p>
      </div>
    );
  }
});
