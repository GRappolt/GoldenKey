package com.roachcitysoftware.goldenkey;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.test.ProviderTestCase2;

import java.net.URI;

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

    public void testBlessingProvider_getTypeDir ()
    {
        String result = mTestProvider.getType(GrandContract.CONTENT_URI_1);
        assertEquals(GrandContract.BLESSING_TYPE_DIR, result);
    }

    public void testBlessingProvider_getTypeItem ()
    {
        android.net.Uri itemUri = ContentUris.withAppendedId(GrandContract.CONTENT_URI_1, 1);
        String result = mTestProvider.getType(itemUri);
        assertEquals(GrandContract.BLESSING_TYPE_ITEM, result);
    }

    public void testBlessingProvider_getTypeInvalid ()
    {
        android.net.Uri itemUri = Uri.parse("content://" +
                "com.marakana.android.yamba.StatusProvider" + "/" + "status");
        String result = "";
        int check = 0;
        try {
            result = mTestProvider.getType(itemUri);
            check = 1;
        } catch (IllegalArgumentException e) {
            check = 2;
        } finally {
            assertEquals(2, check);
            assertEquals("", result);
        }
    }

    public void testBlessingProvider_onAdd ()
    {
        boolean result = mTestProvider.onAdd("testString");
        assertTrue("onAdd failed!", result);
    }
}
