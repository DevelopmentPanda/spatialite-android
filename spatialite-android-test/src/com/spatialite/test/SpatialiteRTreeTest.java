package com.spatialite.test;

import java.io.File;

import jsqlite.Stmt;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

public class SpatialiteRTreeTest extends AndroidTestCase {
	private static final String TAG = "SpatialiteRTreeTest";
	jsqlite.Database db = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbSetUp();
	}

	@Override
	protected void tearDown() throws Exception {
		dbTeardown();
		super.tearDown();
	}

	private void dbTeardown() throws Exception {
		db.close();
		deleteDatabase();
	}

	private void dbSetUp() throws Exception {
		deleteDatabase();

		db = new jsqlite.Database();
		db.open(Environment.getExternalStorageDirectory()
				+ "/Download/testdb.sqlite",
				jsqlite.Constants.SQLITE_OPEN_CREATE);
		
		db.spatialite_create();
	}

	private void deleteDatabase() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Download/testdb.sqlite");
		if (file.exists()) {
			file.delete();
		}
	}
	
	private void insertSpatialData() throws Exception {
		Stmt stmt01 = db.prepare("INSERT INTO test_geom (name, measured_value, the_geom) VALUES (?,?,GeomFromText(?, 4326));");
		stmt01.bind(1, "first point");
		stmt01.bind(2, 1.23456);
		stmt01.bind(3, "POINT(1.01 2.02)");
		stmt01.step();
		
		stmt01.clear_bindings();
		stmt01.reset();
		
		stmt01.bind(1, "second point");
		stmt01.bind(2, 2.34567);
		stmt01.bind(3, "POINT(2.02 3.03)");
		stmt01.step();
		
		stmt01.clear_bindings();
		stmt01.reset();
		
		stmt01.bind(1, "third point");
		stmt01.bind(2, 3.45678);
		stmt01.bind(3, "POINT(3.03 4.04)");
		stmt01.step();
		
		stmt01.close();
	}
	
	public void testCreateSpatialTableWithIndex() throws Exception {
		db.exec("CREATE TABLE test_geom (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, measured_value DOUBLE NOT NULL);",
				null);
			
		db.exec("SELECT AddGeometryColumn('test_geom', 'the_geom', 4326, 'POINT', 'XY');",null);
		
		db.exec("SELECT CreateSpatialIndex('test_geom', 'the_geom');", null);
		
		insertSpatialData();
		
		Stmt stmt01 = db.prepare("SELECT id, name, measured_value, AsText(the_geom) from test_geom");
		while (stmt01.step()) {
			Log.d(TAG, stmt01.column_string(0) + ", " + stmt01.column_string(1) + ", " + stmt01.column_string(2) + ", " + stmt01.column_string(3));
		}
	}
}
