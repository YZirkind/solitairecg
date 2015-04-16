/*
  Copyright 2015 Curtis Gedak

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package net.sourceforge.solitaire_cg;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Readme {

  public Readme(final SolitaireCG solitaire, final DrawMaster drawMaster) {

    solitaire.setContentView(R.layout.readme);
    View view = (View) solitaire.findViewById(R.id.readme_view);
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);

    TextView tv = (TextView) solitaire.findViewById(R.id.body_text);
    tv.setText(Utils.readRawTextFile(solitaire, R.raw.readme));

    final Button close = (Button) solitaire.findViewById(R.id.button_close);
    close.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        solitaire.CancelOptions();
      }
    });

    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
          case KeyEvent.KEYCODE_BACK:
          case KeyEvent.KEYCODE_HOME:
            solitaire.CancelOptions();
            return true;
        }
        return false;
      }
    });
    view.requestFocus();
  }

}

