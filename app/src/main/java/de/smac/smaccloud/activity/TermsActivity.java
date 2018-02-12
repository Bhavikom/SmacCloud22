package de.smac.smaccloud.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.TermsConditionListAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.TermsAndCondition;

public class TermsActivity extends Activity
{
    ListView listView;
    List<TermsAndCondition> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        String[] titles = new String[]{"", getString(R.string.title1), getString(R.string.title2), getString(R.string.title3), getString(R.string.title4), getString(R.string.title5)};
        String[] descriptions = new String[]{getString(R.string.description), getString(R.string.description1), getString(R.string.description2), getString(R.string.description3), getString(R.string.description4), getString(R.string.description5)};



        rowItems = new ArrayList<TermsAndCondition>();
        for (int i = 0; i < titles.length; i++)
        {
            TermsAndCondition item = new TermsAndCondition(titles[i], descriptions[i]);
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.termsAndConditionListView);
        TermsConditionListAdapter adapter = new TermsConditionListAdapter(this,
                R.layout.activity_termslist, rowItems);
        listView.setAdapter(adapter);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_terms));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
