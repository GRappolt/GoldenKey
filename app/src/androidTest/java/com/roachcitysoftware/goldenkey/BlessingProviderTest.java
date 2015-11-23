package com.roachcitysoftware.goldenkey;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.test.ProviderTestCase2;

/**
 * Unit tests for BlessingProvider by GeorgeR created 11/23/2015.
 */
public class BlessingProviderTest extends ProviderTestCase2<BlessingProvider>
{
    private static ContentResolver mSqlContentResolver;
    private static BlessingProvider mTestProvider;

    public BlessingProviderTest () { super(BlessingProvider.class, GrandContract.AUTHORITY_1); }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String sql = String.format("create table %s (%s int primary key, %s text)",
                GrandContract.TABLE_1, GrandContract.BlessingsColumn.ID,
                GrandContract.BlessingsColumn.BLESSING);
        mSqlContentResolver =  newResolverWithContentProviderFromSql (getContext(), "providerTest",
                BlessingProvider.class, GrandContract.AUTHORITY_1, GrandContract.DB_NAME,
                GrandContract.DB_VERSION, sql);
        ContentProviderClient cpc =
                mSqlContentResolver.acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpc == null) {
            return;
        }
        mTestProvider = (BlessingProvider) cpc.getLocalContentProvider();
        cpc.release();
    }

    public void testPreconditions() {
        assertNotNull("mSqlContentResolver is null.", mSqlContentResolver);
        assertNotNull("mTestProvider is null.", mTestProvider);
    }

}
