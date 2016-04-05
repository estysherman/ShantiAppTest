package webit.android.shanti.main.info;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import webit.android.shanti.R;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.map.MapFragment;

/**
 * Created by Android2 on 26/05/2015.
 */
public class InfoMainFragment extends MainBaseFragment {


    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        view.findViewById(R.id.infoActionMapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(MapFragment.getInstance());
            }
        });

        setBackView(view.findViewById(R.id.infoActionBackBtn));
    }

    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        setActionBarEvents(view);
        ((TextView) view.findViewById(R.id.infoActionTitle)).setText(title);
    }


    public static Fragment getInstance() {
        return PlacesFragment.getInstance();
    }
}