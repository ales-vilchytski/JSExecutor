//simple module with no dependencies
//just do something with standard objects
//return exports.success = true

function factorial(n) {
	if (n > 0) {
		return factorial(n-1);
	} else {
		return 1;
	}
}

var str = String(factorial(3));

exports.success = true;