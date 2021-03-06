<!---Start Audio Player--->
<div id="audio-player">
    <div id="audio-top-bar" class="click-area">

        <!--<button class="skip-button" id="skip-back-button"><img src="/images/skip_button.png"/></button>-->
        <!--<button class="skip-button" id="skip-forward-button"><img src="/images/skip_button.png"/></button>-->
        <audio preload="auto">
            <source src="" type="audio/mpeg"/>
        </audio>
    </div>
    <div id="audio-player-content">
        <div id="audio-player-comment-area">
        </div>
    </div>
  <div id="audio-bottom-bar">
    <div style="float:left;width:60px;height:60px;">
      <image id="podcast-art"></image>
      <!--<div id="audio-player-time-stamp">0:00/0:00</div>-->
    </div>
    <div style="overflow:hidden">
      <div id="audio-player-title-area">
          <div class="click-area">
              <div id="now-playing"></div>
          </div>
      </div>
      <button id="play-button"><img src="/images/play_button.png"/></button>
      <div id="audio-player-scrubber" style="display:inline">
          <div id="audio-scrub-bar">
              <div id="audio-scrub-circle">
              </div>
          </div>
      </div>
    </div>
  </div>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
  <script src="../js/audioplayer.js"></script>
</div>

<!---End Audio Player----->
