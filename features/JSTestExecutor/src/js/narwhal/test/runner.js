/*
Copyright (c) 2009, 280 North Inc. <[280north.com](http://280north.com/)\>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to
deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// -- tlrobinson Tom Robinson

var system = require('system');
var file = require('file');
var util = require('util');
var assert = require("./assert");

var stream = require('term').stream;

var args = require('args');
var parser = exports.parser = new args.Parser();
parser.option('--no-color', 'color').def(true).set(false);
parser.option('--loop', 'loop').def(false).set(true);
parser.option('--stacktrace', 'showStacktraces').def(false).set(true);

function getBacktrace(e) {
    if (!e) {
        return "";
    }
    else if (e.rhinoException) {
        var s = new Packages.java.io.StringWriter();
        e.rhinoException.printStackTrace(new Packages.java.io.PrintWriter(s));
        return String(s.toString());
    }
    else if (e.javaException) {
        var s = new Packages.java.io.StringWriter();
        e.javaException.printStackTrace(new Packages.java.io.PrintWriter(s));
        return String(s.toString());
    }
    return "";
}

exports.run = function(objectOrModule) {
    var options = parser.parse([module.path].concat(system.args));
    if (options.color == false)
        stream.disable();
    if (!objectOrModule) {
        var id = file.canonical(options.args.shift());
        objectOrModule = require(id);
    }
    
    do {
        var result = _run(objectOrModule, options);
    } while (options.loop);
    
    return result;
}

var _run = function(objectOrModule, options, context) {

    if (typeof objectOrModule === "string")
        objectOrModule = require(objectOrModule);

    if (!objectOrModule)
        throw "Nothing to run";

    var localContext = context || { passed : 0, failed : 0, error : 0, depth : 0 };
    localContext.depth++;
    
    for (var spaces=""; spaces.length < localContext.depth * 2; spaces += "  ");
    
    for (var property in objectOrModule) {
        if (property.match(/^test/)) {
            print(spaces + "+ Running "+property);
            if (typeof objectOrModule[property] == "function") {
                if (typeof objectOrModule.setup === "function")
                    objectOrModule.setup();

                var globals = {};
                for (var name in system.global) {
                    globals[name] = true;
                }

                try {
                    try {
                        objectOrModule[property]();
                    } finally {
                        if (!objectOrModule.addsGlobals) {
                            for (var name in system.global) {
                                if (!globals[name]) {
                                    delete system.global[name];
                                    throw new assert.AssertionError("New global introduced: " + util.enquote(name));
                                }
                            }
                        }
                    }

                    localContext.passed++;
                } catch (e) {
                    if (e.name === "AssertionError") {
                        var backtrace = getBacktrace(e);
                        var message = "Assertion failed in "+property+":";
                        
                        stream.print("\0violet("+message+"\0)");
                        stream.print("\0yellow("+e+"\0)");
                        if (options.showStacktraces && backtrace)
                            stream.print("\0blue("+backtrace+"\0)");

                        localContext.failed++;
                    } else {    
                        var backtrace = getBacktrace(e);
                        var message = "Exception in "+property+":";
                        
                        stream.print("\0violet("+message+"\0)");
                        stream.print("\0red("+e+"\0)");
                        if (backtrace)
                            stream.print("\0blue("+backtrace+"\0)");
                        
                        localContext.error++;
                    }
                } finally {
                    if (typeof objectOrModule.teardown === "function")
                        objectOrModule.teardown();
                }
            } else {
                _run(objectOrModule[property], options, localContext);
            }
        }
    }
    
    localContext.depth--;

    if (context === undefined)
        print("Passed "+localContext.passed+"; Failed "+localContext.failed+"; Error "+localContext.error+";");

    return localContext.failed + localContext.error;
};

if (require.main == module.id)
    exports.run();

