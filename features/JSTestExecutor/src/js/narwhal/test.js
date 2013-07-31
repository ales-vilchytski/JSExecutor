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

// -- zaach Zachary Carter
// -- kriskowal Kris Kowal Copyright (C) 2009-2010 MIT License

/*
 * This file was revised for Mozilla Rhino 1.7R4
 * Changes:
 *  - module can't be executed as main
 *  - stack tracing changed according Rhino specific
 *  - logging format adopted for Windows terminal
 *  - logging logic changed like JUnit (executes test then print result)
 *  
 *  TODO add html report feature
 */

var ASSERT = require("assert");
var jsDump = require("test/jsdump").jsDump;

exports.run = function(test, opts) {
	if (!test) {
		test = require(test);
	}
	if (!opts) {
		opts = {};
	}
	(opts.title) ? (opts) : (opts.title = '');
	(opts.showPasses) ? (opts) : (opts.showPasses = true);
	(opts.showStackTraces) ? (opts) : (opts.showStackTraces = true);
	
	var log = new exports.Log(opts.title, opts);
	
	_run(test, log);

	log.report();

	return log.fails + log.errors;
}

var _run = function (test, log, options) {

    if (typeof test === "string")
        test = require(test);

    if (!test)
        throw "Nothing to run";

    for (var property in test) {
        if (property.match(/^test/)) {

            var section = log.section(property);
            // alternate logging assertions for those who care
            // to use them.
            var assert = section.Assert();

            if (typeof test[property] == "function") {
                if (typeof test.setup === "function")
                    test.setup();

                try {
                   test[property](assert);
                   
                   if (!section.passes) {
                       section.pass();
                   }
                } catch (e) {
                    if (e.name === "AssertionError") {
                        section.fail(e);
                    } else {    
                        section.error(e);
                    }
                } finally {
                    if (typeof test.teardown === "function")
                        test.teardown();
                    
                    section.flush();
                }
            } else {
                _run(test[property], section, options);
            }
        }
    }
};

function getStackTrace(e) {
    if (!e) {
    return "";
}
else if (e instanceof java.lang.Exception) {
    var s = new Packages.java.io.StringWriter();
    e.printStackTrace(new Packages.java.io.PrintWriter(s));
    return String(s.toString());
}
else if (e.stack) {
    return String(e.message + "\n" + e.stack);
} else {
    return String(e);
}
return "";
}

/*
Log API as applied by the generic test runner:
  log.pass()
  log.fail(assertion)
  log.error(exception)
  log.flush() - writes actual info to stream, can be called once
  log.section(name) :Log
        
Report format:
 <PASS | FAIL | ERROR> test n 
   [message]
 <PASS | FAIL | ERROR> test n + 1 
   [message]
   <PASS | FAIL | ERROR> test n(m) 
     [message]
  ...
*/

exports.Log = function (name, options, stream, parent, root) {
    if (!stream) {
        stream = java.lang.System.out;
    }
    this.options = options;
    this.stream = stream;
    this.name = name;
    this.parent = parent;
    this.root = root || this;
    this.passes = 0;
    this.fails = 0;
    this.errors = 0;

    this.indent = (parent && parent.indent != null) ? (parent.indent + "  ") : ("");
    this.content = { 
        status: 'TestSuite', //will be overwritten if this log is for actual test
        name: (this.name ? this.name : ""), 
        message : []
    };
};

exports.Log.prototype.flush = function () {
	if (!this.flushed) {
        this.flushed = true;
        if (this.parent) {
            this.parent.flush();
        }
    
        this.stream.print(
            this.indent.replace(/ /g, '.') +
            this.content.status + " " + 
            this.content.name);
        
        parts = this.content.message.join('').split(/\n/g);
        if (parts.length > 0) {
        	parts.slice(0, -1).forEach(function (line) {
                this.stream.print(this.indent + "    " + line + "\n");
        	}, this);
        	last = 	parts.pop();
        	if (last && last != '') { 
                this.stream.print(this.indent + "    " + last);
        	}
        }
        this.stream.print("\n");
    }
}

exports.Log.prototype.pass = function () {
    this.passes += 1;
    this.root.passes += 1;
    this.content.status = "PASS";
};

exports.Log.prototype.fail = function (exception) {
    this.fails += 1;
    this.root.fails += 1;

    this.content.status = "FAIL";
    if (exception.message) {
    	 this.content.message.push("\n" + exception.message);
    }
    
    this.content.message.push("\nExpected: " + jsDump.parse(exception.expected));
	this.content.message.push("; Actual: " + jsDump.parse(exception.actual));
    if (exception.operator) {
    	this.content.message.push("; Operator: " + exception.operator);
    }
};

exports.Log.prototype.error = function (exception) {
    this.errors += 1;
    this.root.errors += 1;

    var stacktrace = getStackTrace(exception);
    
    this.content.status = "ERROR";
    if (!this.options.showStackTraces) { //stack trace usually contains message
        this.content.message.push("\n" + exception);
    } else {
    	this.content.message.push("\n" + stacktrace);
    }
};

exports.Log.prototype.report = function () {
    this.stream.print([
        "Passes: " + this.passes,
        "Fails: " + this.fails,
        "Errors: " + this.errors
    ].join(", "));
};

exports.Log.prototype.section = function (name) {
    return new exports.Log(name, this.options, this.stream, this, this.root);
};

exports.Log.prototype.Assert = function () {
    if (!this.assert)
        this.assert = new ASSERT.Assert(this);
    return this.assert;
};
