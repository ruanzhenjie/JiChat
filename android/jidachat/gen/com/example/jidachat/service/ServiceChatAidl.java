/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Administrator\\workspace\\jidachat\\src\\com\\example\\jidachat\\service\\ServiceChatAidl.aidl
 */
package com.example.jidachat.service;
public interface ServiceChatAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.jidachat.service.ServiceChatAidl
{
private static final java.lang.String DESCRIPTOR = "com.example.jidachat.service.ServiceChatAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.jidachat.service.ServiceChatAidl interface,
 * generating a proxy if needed.
 */
public static com.example.jidachat.service.ServiceChatAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.jidachat.service.ServiceChatAidl))) {
return ((com.example.jidachat.service.ServiceChatAidl)iin);
}
return new com.example.jidachat.service.ServiceChatAidl.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_increase:
{
data.enforceInterface(DESCRIPTOR);
this.increase();
reply.writeNoException();
return true;
}
case TRANSACTION_decrease:
{
data.enforceInterface(DESCRIPTOR);
this.decrease();
reply.writeNoException();
return true;
}
case TRANSACTION_getLoginInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getLoginInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.jidachat.service.ServiceChatAidl
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void increase() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_increase, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void decrease() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_decrease, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getLoginInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLoginInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_increase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_decrease = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getLoginInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void increase() throws android.os.RemoteException;
public void decrease() throws android.os.RemoteException;
public java.lang.String getLoginInfo() throws android.os.RemoteException;
}
