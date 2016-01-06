package com.hdfc.libs;

import android.content.Context;
import android.os.Handler;

import com.hdfc.config.Config;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import org.json.JSONObject;

public class AsyncApp42ServiceApi {

	private UserService userService;
	private StorageService storageService;
	private static AsyncApp42ServiceApi mInstance = null;
	
	private AsyncApp42ServiceApi(Context context) {
		App42API.initialize(context, Config.apiKey, Config.apiSecret);
		this.userService = App42API.buildUserService();
		this.storageService = App42API.buildStorageService();
	}

	public static AsyncApp42ServiceApi instance(Context context) {

		if (mInstance == null) {
			mInstance = new AsyncApp42ServiceApi(context);
		}

		return mInstance;
	}


	public void createUser(final String name, final String pswd,
			final String email, final App42UserServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final User user = userService.createUser(name, pswd, email);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUserCreated(user);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onCreationFailed(ex);
							}
						}
					});

				}
			}
		}.start();
	}
	

	public void authenticateUser(final String name, final String pswd,
			final App42UserServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final User response = userService.authenticate(name, pswd);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUserAuthenticated(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onAuthenticationFailed(ex);
							}
						}
					});
				}
			}
		}.start();
	}


	public void getUser(final String name, final App42UserServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final User response = userService.getUser(name);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onGetUserSuccess(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onGetUserFailed(ex);
							}
						}
					});
				}
			}
		}.start();
	}

	public static interface App42UserServiceListener {
		public void onUserCreated(User response);

		public void onCreationFailed(App42Exception exception);

		public void onGetUserSuccess(User response);

		public void onGetUserFailed(App42Exception exception);

		public void onUserAuthenticated(User response);

		public void onAuthenticationFailed(App42Exception exception);

	}


	public void insertJSONDoc(final String dbName, final String collectionName,
			final JSONObject json, final App42StorageServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final Storage response = storageService.insertJSONDocument(dbName, collectionName, json);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onDocumentInserted(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onInsertionFailed(ex);
							}
						}
					});
				}
			}
		}.start();
	}

	public void findDocByDocId(final String dbName, final String collectionName,
			final String docId, final App42StorageServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final Storage response = storageService.findDocumentById(dbName, collectionName, docId);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFindDocSuccess(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onFindDocFailed(ex);
							}
						}
					});
				}
			}
		}.start();
	}


	public void updateDocByKeyValue(final String dbName,
			final String collectionName, final String key, final String value,
			final JSONObject newJsonDoc, final App42StorageServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final Storage response = storageService.updateDocumentByKeyValue(dbName, collectionName, key, value, newJsonDoc);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUpdateDocSuccess(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onUpdateDocFailed(ex);
							}
						}
					});
				}
			}
		}.start();
	}

	public static interface App42StorageServiceListener
	{

		public void onDocumentInserted(Storage response);

		public void onUpdateDocSuccess(Storage response);

		public void onFindDocSuccess(Storage response);

		public void onInsertionFailed(App42Exception ex);

		public void onFindDocFailed(App42Exception ex);

		public void onUpdateDocFailed(App42Exception ex);
	}
}