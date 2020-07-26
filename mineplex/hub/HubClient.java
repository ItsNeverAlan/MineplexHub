package mineplex.hub;

public class HubClient
{
  public String ScoreboardString = "         Hello, I am a big friendly cat!";
  public int ScoreboardIndex = 0;
  
  public String NewsString = "         Bridges v2.0 is coming soon! New gameplay, new kits, new maps!";
  public int NewsIndex = 0;
  
  public String PurchaseString = "         Purchase Ultra Rank at mineplex.com to unlock all game benefits!";
  public int PurchaseIndex = 0;
  
  public String UltraString = "         Thank you for your support!";
  public int UltraIndex = 0;
  
  public String StaffString = "None";
  public int StaffIndex = 0;
  
  public String BestPig = "0-Nobody";
  
  public int DisplayLength = 16;
  
  private int _lastGemCount = 0;
  
  public HubClient(String name)
  {
    this.ScoreboardString = ("      Welcome " + name + ", to the Mineplex Network!");
  }
  
  public void SetLastGemCount(int gems)
  {
    this._lastGemCount = gems;
  }
  
  public int GetLastGemCount()
  {
    return this._lastGemCount;
  }
  
  public String GetScoreboardText()
  {
    if (this.ScoreboardString.length() <= this.DisplayLength) {
      return this.ScoreboardString;
    }
    String display = this.ScoreboardString.substring(this.ScoreboardIndex, Math.min(this.ScoreboardIndex + this.DisplayLength, this.ScoreboardString.length()));
    
    if ((display.length() < this.DisplayLength) && (this.ScoreboardString.length() > this.DisplayLength))
    {
      int add = this.DisplayLength - display.length();
      display = display + this.ScoreboardString.substring(0, add);
    }
    
    this.ScoreboardIndex = ((this.ScoreboardIndex + 1) % this.ScoreboardString.length());
    
    return display;
  }
  
  public String GetPurchaseText(boolean increment)
  {
    if (this.PurchaseString.length() <= this.DisplayLength) {
      return this.PurchaseString;
    }
    if (increment) {
      this.PurchaseIndex = ((this.PurchaseIndex + 1) % this.PurchaseString.length());
    }
    String display = this.PurchaseString.substring(this.PurchaseIndex, Math.min(this.PurchaseIndex + this.DisplayLength, this.PurchaseString.length()));
    
    if ((display.length() < this.DisplayLength) && (this.PurchaseString.length() > this.DisplayLength))
    {
      int add = this.DisplayLength - display.length();
      display = display + this.PurchaseString.substring(0, add);
    }
    
    return display;
  }
  
  public String GetUltraText(boolean increment)
  {
    if (this.UltraString.length() <= this.DisplayLength) {
      return this.UltraString;
    }
    if (increment) {
      this.UltraIndex = ((this.UltraIndex + 1) % this.UltraString.length());
    }
    String display = this.UltraString.substring(this.UltraIndex, Math.min(this.UltraIndex + this.DisplayLength, this.UltraString.length()));
    
    if (display.length() < this.DisplayLength)
    {
      int add = this.DisplayLength - display.length();
      display = display + this.UltraString.substring(0, add);
    }
    
    return display;
  }
  
  public String GetStaffText(boolean increment)
  {
    if (this.StaffString.length() <= this.DisplayLength) {
      return this.StaffString;
    }
    if (increment) {
      this.StaffIndex = ((this.StaffIndex + 1) % this.StaffString.length());
    }
    String display = this.StaffString.substring(this.StaffIndex, Math.min(this.StaffIndex + this.DisplayLength, this.StaffString.length()));
    
    if ((display.length() < this.DisplayLength) && (this.StaffString.length() > this.DisplayLength))
    {
      int add = this.DisplayLength - display.length();
      display = display + this.StaffString.substring(0, add);
    }
    
    return display;
  }
  
  public String GetNewsText(boolean increment)
  {
    if (this.NewsString.length() <= this.DisplayLength) {
      return this.NewsString;
    }
    if (increment) {
      this.NewsIndex = ((this.NewsIndex + 1) % this.NewsString.length());
    }
    String display = this.NewsString.substring(this.NewsIndex, Math.min(this.NewsIndex + this.DisplayLength, this.NewsString.length()));
    
    if ((display.length() < this.DisplayLength) && (this.NewsString.length() > this.DisplayLength))
    {
      int add = this.DisplayLength - display.length();
      display = display + this.NewsString.substring(0, add);
    }
    
    return display;
  }
}
