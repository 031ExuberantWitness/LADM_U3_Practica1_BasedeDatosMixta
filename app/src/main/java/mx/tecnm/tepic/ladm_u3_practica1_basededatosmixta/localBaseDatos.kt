package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class localBaseDatos (context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE CANDIDATOS(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "NOMBRE VARCHAR(400), " +
                    "PREPARATORIA VARCHAR(400), " +
                    "CELULAR VARCHAR(400), " +
                    "CARRERA1 VARCHAR(400), " +
                    "CARRERA2 VARCHAR(400), " +
                    "CORREO VARCHAR(400), " +
                    "FECHA VARCHAR(400))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}