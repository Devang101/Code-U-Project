
import org.jsoup.select.Elements;

//node to return data so that you don't have to fetch and download a wikiPage multiple times
public class DataNode
{
	private Elements paragraphs;
	private int translations;

	public DataNode(){
		empty();
	};
	
	public DataNode(Elements paras, int trans){
		this.paragraphs = paras;
		this.translations = trans;
	}
	
	public void empty(){
		this.paragraphs = null;
		this.translations = 0;
	}
	
	public Elements getParagraphs(){
		return this.paragraphs;
	}
	
	public int getTranslations(){
		return this.translations;
	}
}
