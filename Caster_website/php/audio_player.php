<!---Start Audio Player--->
<div id="audio-player">
    <div id="audio-top-bar">  
        <button id="play-button"><img src="/images/play_button.png"/></button>    
        <button class="skip-button" id="skip-back-button"><img src="/images/skip_button.png"/></button>
        <div id="audio-player-title-area">
            <div class="click-area">
                <div id="now-playing">Now Playing:</div>    
            </div>            
        </div>
        <button class="skip-button" id="skip-forward-button"><img src="/images/skip_button.png"/></button>
        <audio preload="auto">
            <source src="" type="audio/mpeg"/>
        </audio>        
    </div>
    <div id="audio-player-scrubber">        
        <div id="audio-player-time-stamp">0:00/0:00</div>
        <div id="audio-scrub-bar">
            <div id="audio-scrub-circle">
            </div>
        </div>
    </div>
    <div id="audio-player-content">              
        <div id="audio-player-comment-area">            
        </div>
    </div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <script src="../js/audioplayer.js"></script>
</div>
<!---End Audio Player----->
