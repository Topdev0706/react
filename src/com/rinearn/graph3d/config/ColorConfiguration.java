package com.rinearn.graph3d.config;

import java.awt.Color;

/*
!!!!!
NOTE
!!!!!

・カラーミキサーのグラデーション設定とかブレンド処理とか、どこに属するべきか？

  -> レンダラーにメソッドがある時点でConfigに持たせるしかない気がする。
     本来オプションからの描画生成は上の層だし、レンダラーからオプションの設定を任意タイミングで読めるのはおかしいし。
     なので、設定の格納はConfig一択なような。引数paramの設定に応じて系列色になったりグラデになったりの実装も考えると。
     (つまり現行版でそれがオプション層にあるのはそもそもなんかおかしい)

  ↑いや、それ自体は、ブレンドの処理と設定メソッドがレンダラー層にあれば、
    上層からオプションON/OFFする度に設定参照＆更新してるって解釈も、仕組み的に可能なのは可能でしょ。
    Config管轄ってのは別にマストではない。レンダラーに直接 Gradient の Setter があってもいい。あくまで理屈の整合上はだけど。

  ↑いやしかし、上限/下限とか、彩色次元とか、色々複雑なパラメータあるし、
    設定構造の規模から考えたらそれ自体をColorConfigとは別にGradientConfigってしても良いレベルかも。むしろ。
    まあ少なくともconfig階層に属すべきだとは思う。規模的に。

  ↑上記の続き:
    表面的なオプション層との対応関係や互換性は、あくまでもカテゴリーごとの保存と再現ができれば実用問題は生じないので、
    エンコーダ/デコーダでどうとでも吸収できる。オプションの保存はOptionConfigでON/OFF状態さえ保存すれば済むし、
    その際にグラデの色設定は ColorConfig に書き込み/読み込みした所で、表面的な問題は何も生じない。中での話で閉じるし。

    なので将来性を考えると内部Configは素直に作っとといた方が良い。設定ファイルは上記の通り、互換はどうにでもできるので。変換も可能だし。
    それに、設定ファイルの中にコメント吐けるので、オプションの下に「これの詳細設定は ... の項目にある」とかも書いてnotationできるし。

    という事でConfig管轄で。

  ↑まあそもそもの原因としては現行版でGradientがオプションにあるのがなんかおかしいので。
    しかし便利さを考えたらそちらのほうが便利かもだけど。
    外から見える層ではまあそれでもいいか。中は中で整頓すれば。必ず内外が対応している必要はない。


・そういえばレンダラー層の param の autoColoringEnabledは、
  Gradientだけでなく系列でのソリッド彩色も（オプション状態に応じて）行われる仕様なんだった。
  つまりカラーミキサはグラデとソリッド彩色の両者を併せて扱わないと。両者を分けるのはまずい。
  とするとConfigも、「ソリッド色設定だけConfigにあってグラデ色設定はオプション側にある」みたいに分かれるのはやはりまずい。

  -> 例えばColorConfigの中にGradientColorConfigとSolidColorConfigがあって、ColoringModeで選択する。
     んでGradientConfigのsetは、オプションUIからもSet Colorメニューからも行えるようにすればOKか。
     保持はConfig層、setはオプションからも可、という。


・グラデには恐らくデフォルト（系列ごと設定が無い場合の採用値）と系列ごとの設定の2通りが必要になるように、ソリッドにも要る？
  引数 param で系列インデックスを指定していない場合にデフォルトが採用される、とかで使える。
  または、指定した系列インデックスに対応する色設定が存在しない場合。

  ↑後者は系列インデックスについて色が循環的になる挙動と整合しなくない？そこは%Nして参照すべきでは？
    > 確かに。

  → ソリッドの登録数とグラデの登録数が同じである必要はないんだし、グラデは1個だけ登録しとけば、
     （i % 1 = i for 任意の i なので）全系列が同じグラデになって、
     その場合デフォルト（系列ごと設定が無い場合の採用値）と同じように機能するでしょ。
     なので、そもそもデフォルトという概念が要らない。1個だけ登録っていう状態で兼ねられる。
 
     んで、そのグラデ設定の1個を、現行版のデフォルトのやつにしとけばいい。
     ソリッドはデフォルトで8個くらい用意しといて。
     そうすりゃ「グラデONだと全系列デフォルトグラデ、OFFだと系列ごと彩色」という現行版の挙動を踏襲しつつ、
     必要に応じて「グラデを系列ごとに分けたい」といった場面での拡張性も持たせられる。

     ↑これいい。たぶんこれを採用する。

     → しかし、ColorMode は系列ごとに設定できるべきだけど、そうするとソリッドとグラデの定義数が同じじゃないといけなくない？

        → colorMode も %N して使う仕様にすればいい。別に同じである必要はない。

           つまり、デフォルトでソリッドが8色、グラデが1色されてたとする。
           この時にユーザーが系列0,1,2までのデータをプロットしていたとして、colorModeは要素数3で指定すれば十分で。
           それ超えたら[0]って循環する。

           ↑ 上の例だとグラデ→ソリッド→ソリッドになって、その後またグラデ→ソリッド→ソリッドになるみたいな。
              んでそれぞれのグラデやソリッドがどの色になるかは、それぞれの色設定に %N して算出される感じで。

              ややこしいけど、まあこの設定の中身はUIが生成するので、UI側で見え方を調整すればいけるか。
              構造的な柔軟性はたぶん高いのであり。


・Gradient、2次元グラデ（レインボー＋透明度）とかもリクエストあったんだし、
  単純なグラデはGradient1Dとかにすべきか。後々で拡張可能にするために。2次元グラデをサポートする時にGradient2D作る。

  ↑確かに、X/Y/Z値でそれぞれ違う成分をグラデさせる、とかもありえるわけだし、Gradient3Dとかも出てくる。

    ↑となると、必然的に Min とか Max とか Dimension は Gradation1D オブジェクトが持つべきか。
      ColorConfig がトップ階層で持つのはよくない。

  → しかし、GradientColorをインターフェースにするよりは、実装にして、
     gradientModeを持たせて分派したロジックも持たせる方がいい気もする。
     Config層でインターフェース＋大量の実装みたいな形は全体把握がうっとうしそうでなんかいやな気がする。

     → しかし実装のすっきり感としては Gradient はインターフェース＋実装分派で分けた方が合ってる気がする。なんとなくだけど。

     ↑ GradientColor がミキサーを兼ねるなら後者だと思う。Configに徹してミキサーは別実装なら前者でもいいかも。


  また実装しながら後々で要検討

!!!!!
NOTE
!!!!!
*/


/**
 * The class for storing configuration of colors.
 */
public class ColorConfiguration {

	/**
	 * Creates new configuration storing default values.
	 */
	public ColorConfiguration() {		
	}
}
