testCases(test, 
	function testNamespace() {
		//declaring global namespace
		namespace('tst.namespace');
		assert.that(typeof tst.namespace, not(eq('undefined')));
		
		tst.namespace.myvar = { f : 1};
		assert.that(tst.namespace.myvar.f, eq(1));
		
		namespace('tst.namespace', function() {
			var _private = { f : 2 };
			this.stub = function() { return _private; };
			this._public = { f : 3 };
		});
		assert.that(typeof tst.namespace._private, eq('undefined'));
		assert.that(tst.namespace._public.f, eq(3));
		
		//declaring local namespace
		namespace.call(this, "local.namespace", function() {
			this._localPublic = { f : 4 };
		});
		assert.that(typeof local, eq('undefined'));
		assert.that(this.local.namespace._localPublic.f, eq(4));
		
		//declaring hierarchical namespaces
		namespace('tst2', function() {
			namespace.call(this, 'namespace', function() {
				this._obj = { f : 5 };
			});
		});
		assert.that(tst2.namespace._obj.f, eq(5));
	}
);