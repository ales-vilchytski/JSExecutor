/**
 * Runs specified test using options from Java map 'options' in scope:
 *  - out - java.io.OutputStream or object supporting print(String) method
 *  - id - id of module with tests
 *  - name - name of running test suite
 *  - showStackTraces - boolean value - use or not stack traces on errors
 *  - showPasses - boolean value - when false then only fails and errors will be output
 */

var test = require('test');
var unit = require(id);

var opts = {
    name : '',
    out : java.lang.System.out,
    showPasses : true
};
for (var i in opts) {
    if (this.options.get(i) != null) {
        opts[i] = this.options.get(i);
    }
}

if(require.main == module) {
    test.run(unit, opts);
}
